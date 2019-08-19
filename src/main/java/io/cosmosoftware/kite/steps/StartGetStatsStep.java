package io.cosmosoftware.kite.steps;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.report.Status;
import org.openqa.selenium.JavascriptExecutor;

import javax.json.JsonArray;
import javax.json.JsonObject;

public class StartGetStatsStep extends TestStep {

  enum SFU {
    DEFAULT,
    JITSI,
    JANUS,
    MEDOOZE,
    MEDIASOUP
  };

  private final int chartsStatsInterval;
  private final SFU sfu;
  private final String peerConnectionScript;

  public StartGetStatsStep(Runner runner, JsonObject getChartsConfig) {
    super(runner);
    this.chartsStatsInterval = getChartsConfig.getInt("chartsStatsInterval", 2) * 1000;
    this.sfu = SFU.valueOf(getChartsConfig.getString("sfu", SFU.DEFAULT.name()));
    this.peerConnectionScript = getPeerConnectionScript(getChartsConfig.getJsonArray("peerConnections"));
  }

  @Override
  public String stepDescription() {
    return "Start of stats recovery";
  }

  @Override
  public void step() throws KiteTestException {
    try {
      ((JavascriptExecutor) webDriver).executeScript(peerConnectionScript);
      ((JavascriptExecutor) webDriver)
          .executeScript(getStartGetStatsDuringTestScript(chartsStatsInterval));

    } catch (Exception e) {
      throw new KiteTestException("Unable to start stats recovery", Status.FAILED, e);
    }
  }

  private String getStartGetStatsDuringTestScript(int interval) {
    return "window.Running = true;"
        + "window.StatsOvertime = [];"
        + "function waitAround(ms) {"
        + "  return new Promise(resolve => setTimeout(resolve, ms));"
        + "}"
        + "async function gettingStatsDuringTest(pcArray) {"
        + "  if (pcArray.length > 0) {"
        + "    let stats;"
        + "    for (let idx = 0; idx < pcArray.length; idx++) {"
        + "      if (typeof window.StatsOvertime[idx] === \"undefined\") {"
        + "        window.StatsOvertime[idx] = [];"
        + "      }"
        + "      stats = await pcArray[idx].getStats().then(data => {"
        + "        let statsObj = [];"
        + "        let presenceOfCodec = false;"
        + "        data.forEach(res => {"
        + "          if(res.type === 'codec') {"
        + "            presenceOfCodec = true;"
        + "          }"
        + "          if(presenceOfCodec && res.type === 'inbound-rtp' && !res.codecId) {"
        + "            "
        + "          } else {"
        + "            statsObj.push(res);"
        + "          }"
        + "        });"
        + "        return statsObj;"
        + "      });"
        + "      window.StatsOvertime[idx].push(stats);"
        + "    }"
        + "  }"
        + "}"
        + "(async () => {"
        + "while (window.Running) {"
        + "    let stats = await gettingStatsDuringTest(window.peerConnections);"
        + "    await waitAround("
        + interval
        + ");"
        + "}"
        + "})();";
  }

  private String getPeerConnectionScript(JsonArray peerConnections) {
    switch (this.sfu) {
      case JITSI:
        {
          return "window.peerConnections = window.pc = [];"
              + "map = APP.conference._room.rtc.peerConnections;"
              + "for(var key of map.keys()){"
              + "  window.pc.push(map.get(key).peerconnection);"
              + "}";
        }
      case JANUS:
      case MEDIASOUP:
      case MEDOOZE:
      default:
        {
          String script = "window.peerConnections = [";
          if (peerConnections != null) {
            for (int i = 0; i < peerConnections.size(); i++) {
              script += peerConnections.get(i).toString().replaceAll("\"", "");
              if (i != peerConnections.size() - 1) {
                script += ",";
              }
            }
          }
          script += "];";
          return script;
        }
    }
  }
}
