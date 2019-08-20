/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.stats;


import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.Map;

/**
 * Utility class with static methods to obtain and process getStats()
 */
public class GetStatsUtils {

  private static final int INTERVAL = 500;

  /**
   * Build a stats Json object from a stats array
   *
   * @param statArray array of stats from js function
   * @return stats JsonObject.
   */
  public static JsonObject buildStatArray(Object statArray) {
    JsonObjectBuilder statObjectBuilder = Json.createObjectBuilder();
    for (Object map : (ArrayList) statArray) {
      if (map != null) {
        Map<Object, Object> statMap = (Map<Object, Object>) map;
        String id = (String) statMap.get("id");
        JsonObjectBuilder tmp = Json.createObjectBuilder();
        if (!statMap.isEmpty()) {
          for (Object item : statMap.keySet()) {
            tmp.add(item.toString(), statMap.get(item).toString());
          }
        }
        statObjectBuilder.add(id, tmp.build());
      }
    }
    return statObjectBuilder.build();
  }

  /**
   * Gets stats once.
   *
   * @param statsType the stats type
   * @param webDriver the web driver
   * @return the stats once
   * @throws InterruptedException the interrupted exception
   */
  public static Object getStatsOnce(String statsType, WebDriver webDriver)
      throws InterruptedException {
    ((JavascriptExecutor) webDriver).executeScript(stashStatsScript(statsType));
    if (statsType.equals("kite")) {
      Thread.sleep(INTERVAL);
    }
    return ((JavascriptExecutor) webDriver).executeScript(getStatsScript(statsType));
  }

  /**
   * @return the JavaScript as string, depending on needed stats.
   */
  private static final String getStatsScript(String statsType) {
    String jsQuery = "";
    switch (statsType) {
      case "kite":
        jsQuery = "return window.KITEStats;";
        break;
      case "local":
        jsQuery = "return window.LocalStats;";
        break;
      case "remote":
        jsQuery = "return window.RemoteStats;";
        break;
      case "jitsi":
        jsQuery = "return window.JitsiStats;";
        break;
    }
    return jsQuery;
  }

  /**
   * Returns JavaScript to collect browser stats using getStats() API
   *
   * @return the JavaScript as string.
   */
  private static final String stashStatsScript() {
    return "function getAllStats() {\n"
        + "    return new Promise( (resolve, reject) => {\n"
        + "        try{\n"
        + "            window.remotePc.getStats().then((report) => {\n"
        + "                let statTypes = new Set();\n"
        + "                // type -> stat1, stat2, stat3\n"
        + "                let statTree = new Map();\n"
        + "                for (let stat of report.values()) {\n"
        + "                    const curType = stat.type;\n"
        + "                    const prvStat = statTree.get(curType);\n"
        + "                    if(prvStat) {\n"
        + "                        const _tmp = [...prvStat, stat];\n"
        + "                        statTree.set(curType, _tmp)\n"
        + "                    }else{\n"
        + "                        const _tmp = [stat];\n"
        + "                        statTree.set(curType, _tmp)\n"
        + "                    }\n"
        + "                }\n"
        + "                let retval = {};\n"
        + "                for (const [key, statsArr] of statTree) {\n"
        + "                    let keysArr = [];\n"
        + "                    for(const curStat of statsArr){\n"
        + "                        const keys = Object.keys(curStat);\n"
        + "                        keysArr = [ ...keysArr, ...keys ];\n"
        + "                    }\n"
        + "                    retval[key] = keysArr;\n"
        + "                }\n"
        + "                resolve(retval);\n"
        + "        });\n"
        + "        } catch(err) {\n"
        + "            reject(err);\n"
        + "        }\n"
        + "    });\n"
        + "}\n"
        + "function stashStats() {\n"
        + "    getAllStats().then( (data)=> {\n"
        + "        window.KITEStatsDiff = data;\n"
        + "    }, err => {\n"
        + "        console.log('error',err);\n"
        + "    });\n"
        + "}\n"
        + "stashStats()\n";
  }

  /**
   * Calls getStats from the peer connection object and stash them in a global variable. JS function
   * depends on the test type.
   *
   * @param statsType type of needed stats: - "kite" for data.values which should be filtered after
   * - "local" for stats from the response of pc.getStats() - "remote" for stats of all remote pc -
   * "jitsi" in the case of a Jitsi test (pc in Jitsi is not exposed as window.pc)
   * @return the stashStatsScript as string.
   */
  private static final String stashStatsScript(String statsType) {
    String jsQuery = "";
    switch (statsType) {
      case "kite":
        jsQuery = "const getStatsValues = () =>"
            + "  pc.getStats()"
            + "    .then(data => {"
            + "      return [...data.values()];"
            + "    });"
            + "const stashStats = async () => {"
            + "  window.KITEStats = await getStatsValues();"
            + "  return window.KITEStats;"
            + "};"
            + "stashStats();";
        break;
      case "local":
        jsQuery = "const getLocalStatsValues = () =>"
            + "  pc.getStats(function (res) {"
            + "            var items = [];"
            + "            res.result().forEach(function (result) {"
            + "                var item = {};"
            + "                result.names().forEach(function (name) {"
            + "                    item[name] = result.stat(name);"
            + "                });"
            + "                item.id = result.id;"
            + "                item.type = result.type;"
            + "                item.timestamp = result.timestamp.getTime().toString();"
            + "                items.push(item);"
            + "            });"
            + "            window.LocalStats = items;"
            + "        });"
            + "const stashLocalStats = async () => {"
            + "  await getLocalStatsValues();"
            + "  return window.LocalStats;"
            + "};"
            + "stashLocalStats();";
        break;
      case "remote":
        jsQuery = "const getRemoteStatsValues = (i) =>"
            + "  remotePc[i].getStats(function (res) {"
            + "            var items = [];"
            + "            res.result().forEach(function (result) {"
            + "                var item = {};"
            + "                result.names().forEach(function (name) {"
            + "                    item[name] = result.stat(name);"
            + "                });"
            + "                item.id = result.id;"
            + "                item.type = result.type;"
            + "                item.timestamp = result.timestamp.getTime().toString();"
            + "                items.push(item);"
            + "            });"
            + "            if (!window.RemoteStats) window.RemoteStats = [items]; "
            + "            else window.RemoteStats.push(items);"
            + "        });"
            + "const stashRemoteStats = async () => {"
            + "  window.RemoteStats = [];"
            + "  for (i in remotePc) await getRemoteStatsValues(i);"
            + "  return window.RemoteStats;"
            + "};"
            + "stashRemoteStats();";
        break;
      case "jitsi":
        jsQuery = "const getJitsiStatsValues = (p) =>"
            + "  p.getStats(function (res) {"
            + "            var items = [];"
            + "            res.result().forEach(function (result) {"
            + "                var item = {};"
            + "                result.names().forEach(function (name) {"
            + "                    item[name] = result.stat(name);"
            + "                });"
            + "                item.id = result.id;"
            + "                item.type = result.type;"
            + "                item.timestamp = result.timestamp.getTime().toString();"
            + "                items.push(item);"
            + "            });"
            + "            if (!window.JitsiStats) window.JitsiStats = [items]; "
            + "            else window.JitsiStats.push(items);"
            + "        });"
            + "const stashJitsiStats = async () => {"
            + "  window.JitsiStats = [];"
            + "  APP.conference._room.rtc.peerConnections.forEach(await function(p){getJitsiStatsValues(p);});"
            + "  return window.JitsiStats;"
            + "};"
            + "stashJitsiStats();";
        break;
    }
    return jsQuery;
  }
}
