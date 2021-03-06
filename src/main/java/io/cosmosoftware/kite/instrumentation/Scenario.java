/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.instrumentation;

import io.cosmosoftware.kite.config.KiteEntity;
import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.manager.SSHManager;
import io.cosmosoftware.kite.report.KiteLogger;
import io.cosmosoftware.kite.report.Status;
import io.cosmosoftware.kite.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Scenario extends KiteEntity {
  
  private static final int DEFAULT_SCENARIO_DURATION = 10000;

  private static final String DEFAULT_GW_NIT1 = "eth1";
  private static final String DEFAULT_GW_NIT2 = "eth2";  
  private static final String DEFAULT_CLIENT_NIT = "eth1";

  private final String type;
  private final String name;
  private final String command;
  private final String cleanUpCommand;
  private final String gateway;
  private final List<String> nit;
  private final Integer duration;
  private final NetworkInstrumentation networkInstrumentation;
  private final KiteLogger logger;
  private final String network;
  private List<Integer> clientIds = Collections.synchronizedList(new ArrayList<>());
  private final String kiteServerUrl;

  public Scenario(JsonObject jsonObject, KiteLogger logger,
      NetworkInstrumentation networkInstrumentation) throws Exception {
    this.logger = logger;
    String missingKey = "";
    this.networkInstrumentation = networkInstrumentation;
    try {
      missingKey = "type";
      this.type = jsonObject.getString("type");
      if (this.type.equals("client")) {
        missingKey = "clientIds";
        JsonArray jsonArray = jsonObject.getJsonArray("clientIds");
        for (int i = 0; i < jsonArray.size(); i++) {
          this.clientIds.add(jsonArray.getInt(i));
        }
        this.gateway = null;
      } else if (this.type.equals("gateway")) {
        missingKey = "gateway";
        this.gateway = jsonObject.getString("gateway");
        this.clientIds = null;
      } else {
        throw new KiteTestException("The type specified doesn't exist", Status.FAILED);
      }
      missingKey = "network";
      this.network = jsonObject.getString("network");
      NetworkProfile networkProfile = this.networkInstrumentation.getNetworkProfile(network);
      if (networkProfile == null) {
        throw new KiteTestException("The NetworkProfile " + network + " is not defined", Status.FAILED);
      }
      this.nit = getNit(jsonObject, this.type);      
      this.command = networkProfile.getCommand(this.nit).trim();
      this.cleanUpCommand = networkProfile.getCleanUpCommand(this.nit).trim();
      missingKey = "name";
      name = jsonObject.getString("name");
    } catch (NullPointerException e) {
      throw new KiteTestException("The key " + missingKey + " is missing", Status.FAILED, e);
    }
    this.duration = jsonObject.getInt("duration", DEFAULT_SCENARIO_DURATION);
    this.kiteServerUrl = networkInstrumentation.getKiteServer();
  }

  public String getName() {
    return name;
  }

  public List<Integer> getClientIds() {
    return clientIds;
  }

  public String getCommand() {
    return command;
  }

  public String getGateway() {
    return gateway;
  }

  public String getType() {
    return type;
  }

  public String getNetwork() { return network; }

  public Integer getDuration() {
    return duration;
  }
  
  private List<String> getNit(JsonObject jsonObject, String type) {
    List<String> result = new ArrayList<>();
    if (!jsonObject.containsKey("nit")) {
      if ("gateway".equalsIgnoreCase(type)) {
        result.add(DEFAULT_GW_NIT1);
        result.add(DEFAULT_GW_NIT2);
      } else {
        result.add(DEFAULT_CLIENT_NIT);
      }
    } else {
      JsonArray jsonArray = jsonObject.getJsonArray("nit");
      for (int i = 0; i < jsonArray.size(); i++) {
        result.add(jsonArray.getString(i));
      }
    }
    return result;
  }

  public String sendCommand(WebDriver webDriver) {
    StringBuilder result = new StringBuilder();
    result.append("Running ").append(this.command).append(" on ");
    if (this.type.equals("gateway")) {
      result.append(this.gateway);
      logger.info(result.toString());
      result.append(this.runCommandGateway(this.command, this.networkInstrumentation));
    }
    if (this.type.equals("client")) {
      String sessionId = ((RemoteWebDriver) webDriver).getSessionId().toString();
      String nodeIp = TestUtils
          .getPrivateIp(this.networkInstrumentation.getRemoteAddress(), sessionId);
      result.append("client ").append(nodeIp);
      if (((RemoteWebDriver) webDriver).getCapabilities().getPlatform().toString()
          .equalsIgnoreCase("LINUX")) {
        result.append(this.runCommandClient(this.command, this.networkInstrumentation, nodeIp));
      } else {
        result.append("Node ").append(nodeIp).append(" is not Linux");
      }
    }
    return result.toString();
  }

  public String cleanUp(WebDriver webDriver) {
    StringBuilder result = new StringBuilder();    
    result.append("Doing CleanUp for ").append(this.command).append(" on ");
    if (this.type.equals("gateway")) {
      result.append(this.gateway);
      logger.info(result.toString());
      result.append(this.runCommandGateway(cleanUpCommand, this.networkInstrumentation));
    }
    if (this.type.equals("client")) {
      String sessionId = ((RemoteWebDriver) webDriver).getSessionId().toString();
      String nodeIp = TestUtils
          .getPrivateIp(this.networkInstrumentation.getRemoteAddress(), sessionId);
      result.append("client ").append(nodeIp);
      if (((RemoteWebDriver) webDriver).getCapabilities().getPlatform().toString()
          .equalsIgnoreCase("LINUX")) {
        result.append(this.runCommandClient(cleanUpCommand, this.networkInstrumentation, nodeIp));
      } else {
        result.append(" FAILED, check instrumentUrl !");
      }
    }
    return result.toString();
  }

  private String sshCommand(Instance instance, String command) {
    String result;
    try {
      SSHManager sshManager = new SSHManager(instance.getKeyFilePath(), instance.getUsername(),
          instance.getIpAddress(), command);
      if (sshManager.call().commandSuccessful()) {
        Thread.sleep(1000);
        result = "SUCCEEDED";
      } else {
        result = "FAILED";
      }
    } catch (Exception e) {
      result = "thrown ERROR : " + e.getLocalizedMessage();
    }
    return result;
  }


  private String runCommandGateway(String command, NetworkInstrumentation networkInstrumentation) {
    StringBuilder result = new StringBuilder();
    String GridId = networkInstrumentation.getKiteServerGridId();
    if (kiteServerUrl == null) {
      Instance instance = networkInstrumentation.getInstances().get(this.gateway);
      result.append(" via ssh ").append(this.sshCommand(instance, command));
    } else {
      try {
        command = URLEncoder.encode(command, "UTF-8");
      } catch (Exception e) {
        logger.info("Error while encoding command: " + command);
      }
      String url = "/command?id=" + GridId + "&gw=" + this.gateway.split("w")[1] + "&cmd=" + command;
      result.append("via KiteServer ").append(TestUtils.kiteServerCommand(kiteServerUrl, url));
    }
    return result.toString();
  }

  private String runCommandClient(String command, NetworkInstrumentation networkInstrumentation,
      String nodeIp) {
    StringBuilder result = new StringBuilder();
    String GridId = networkInstrumentation.getKiteServerGridId();
    try {
      command = URLEncoder.encode(command, "UTF-8");
    } catch (Exception e) {
      logger.info("Error while encoding command: " + command);
    }
    String url = "/command?id=" + GridId + "&ip=" + nodeIp + "&cmd=" + command;
    result.append(TestUtils.kiteServerCommand(kiteServerUrl, url));

    return result.toString();
  }

  public boolean shouldExecute(int clientId) {
    return (this.getType().equals("gateway") && clientId == 0) || (this.getType().equals("client")
        && this.getClientIds().contains(clientId));
  }
}
