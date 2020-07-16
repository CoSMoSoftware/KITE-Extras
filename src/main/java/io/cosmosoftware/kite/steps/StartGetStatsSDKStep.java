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
//  private String sfu;
  private int statsPublishingInterval;
  private String userNameCommand;
  private String customizedUserId;
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
    this.statsPublishingInterval = getStatsSdk.getInt("statsPublishingInterval", 30000);
    this.userNameCommand = getStatsSdk.getString("userNameCommand", null);
    this.roomNameCommand = getStatsSdk.getString("roomNameCommand", "\"unknown-room\"");
    this.customizedUserId = (this.customizedUserId != null ? this.customizedUserId : "")
        + (this.report.getClientId() == null ? "unknown" : report.getClientId());
  }

  @Override
  public String stepDescription() {
    return "Loading GetStats Script for client";
  }

  @Override
  protected void step() throws KiteTestException {
    this.init();
    logger.debug("Attempting to load GetStats script " + this.customizedUserId + " (every " + this.statsPublishingInterval + "ms)");
    if (this.userNameCommand == null) {
      this.userNameCommand = "\""
          + this.customizedUserId
          + "\"";
    }
    for (String pc : this.pcList) {
      String userName = this.userNameCommand;
      if (userName.startsWith("\"")) {
        userName = userName.substring(0, userName.length() - 2)
            + "[" + pc + "]"
            + "\"";
      }
      loadGetStats(logstashUrl,  pc, testName,  userName , roomNameCommand, statsPublishingInterval);
    }
    waitAround(10000);
  }

  private String loadGetStats(
      String logstashUrl,
      String pc,
      String testName,
      String userNameCommand,
      String roomNameCommand,
      int statsPublishingInterval
  ) throws KiteTestException {
    String getStatString = getStatsSdkString(logstashUrl, pc, testName, userNameCommand, roomNameCommand, statsPublishingInterval);
    logger.debug("String ready, executing getstats script for " + pc + ": " + getStatString);
    return (String) executeJsScript(webDriver, getStatString);
  }

  public void setCustomizedUserId(String customizedUserId) {
    this.customizedUserId = customizedUserId;
  }
}