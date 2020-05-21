package io.cosmosoftware.kite.steps;

import static io.cosmosoftware.kite.action.JSActionScript.getStatsSdkString;
import static io.cosmosoftware.kite.util.TestUtils.executeJsScript;
import static io.cosmosoftware.kite.util.TestUtils.waitAround;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.json.JsonObject;

public class StartGetStatsSDKStep extends TestStep {
  private final JsonObject getStatsSdk;
  private final JsonObject getStatsConfig;
  private final String testName;
  private String testId;
  private String logstashUrl;
  private String sfu;
  private int statsPublishingInterval;
  private String userNameCommand;
  private String roomNameCommand;
  private List<String> pcList = new ArrayList<>();

  public StartGetStatsSDKStep(Runner runner, String testName, JsonObject getStatsSdk, JsonObject getStatsConfig) {
    super(runner);
    this.testName = testName;
    this.getStatsSdk = getStatsSdk;
    this.getStatsConfig = getStatsConfig;
    // get stat config part
  }

  private void init() {
    for (int i = 0; i < getStatsConfig.getJsonArray("peerConnections").size(); i++) {
      pcList.add(getStatsConfig.getJsonArray("peerConnections").getString(i));
    }
    // get stat sdk part

    this.testId =  getStatsSdk.getString("testId", testName + "_"
        + new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date()) ) ;
    this.logstashUrl = getStatsSdk.getString("logstashUrl");
    this.sfu = getStatsSdk.getString("sfu", "unknown-sfu");
    this.statsPublishingInterval = getStatsSdk.getInt("statsPublishingInterval", 30000);
    this.userNameCommand = getStatsSdk.getString("userNameCommand",
        "\"" + (this.report.getClientId() == null ? "unknown" : report.getClientId()) + "\"");
    this.roomNameCommand = getStatsSdk.getString("roomNameCommand", "\"unknown-room\"");
  }

  @Override
  public String stepDescription() {
    return "Loading GetStats Script for " + testId;
  }

  @Override
  protected void step() throws KiteTestException {
    this.init();
    logger.info("Attempting to load GetStats script");
    for (String pc : this.pcList) {
      loadGetStats(logstashUrl, sfu, pc, testName, testId, userNameCommand, roomNameCommand, statsPublishingInterval);
    }
    waitAround(10000);
  }

  private String loadGetStats(
      String logstashUrl,
      String sfu,
      String pc,
      String testName,
      String testId,
      String userNameCommand,
      String roomNameCommand,
      int statsPublishingInterval
  ) throws KiteTestException {
    String getStatString = getStatsSdkString(logstashUrl, sfu, pc, testName, testId, userNameCommand, roomNameCommand, statsPublishingInterval);
    logger.debug("String ready, executing Javascript script" + getStatString);
    String result = (String) executeJsScript(webDriver, getStatString);
    return result;
  }
  

}