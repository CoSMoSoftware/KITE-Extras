/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.instrumentation;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.manager.SSHManager;
import io.cosmosoftware.kite.report.Status;
import io.cosmosoftware.kite.util.TestUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Scenario {

  private static final int DEFAULT_SCENARIO_DURATION = 10000;
  
  private final String type;
  private final String name;
  private final String command;
  private final String network;
  private final String gateway;
  private final String nit;
  private final Integer duration;
  private ArrayList<Integer> clientIds = new ArrayList<>();
  private final NetworkProfileHashMap networkProfileHashMap;
  private final Instrumentation instrumentation;
  private final Logger logger;

  public Scenario(JsonObject jsonObject, Logger logger, NetworkProfileHashMap networkProfileHashMap, Instrumentation instrumentation) throws Exception {
    this.logger = logger;
    String missingKey="";
    this.networkProfileHashMap = networkProfileHashMap;
    this.instrumentation = instrumentation;
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
      this.command =  this.networkProfileHashMap.get(this.network).getCommand().trim();
      this.nit = this.networkProfileHashMap.get(this.network).getInterface();
      missingKey = "name";
      name = jsonObject.getString("name");
    } catch (Exception e) {
      throw new KiteTestException("The key " + missingKey + " is missing", Status.FAILED, e);
    }
    this.duration = jsonObject.getInt("duration", DEFAULT_SCENARIO_DURATION);
  }

  public String getName() {
    return name;
  }

  public ArrayList<Integer> getClientIds() {
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

  public Integer getDuration() { return duration; }

  public String sendCommand(WebDriver webDriver) {
    StringBuilder result = new StringBuilder();
    result.append("Running ").append(this.command).append(" on ");
    if (this.type.equals("gateway")) {
      result.append(this.gateway);
      logger.info(result.toString());
      result.append(this.runCommandGateway(this.command, this.instrumentation));
    }
    if (this.type.equals("client")) {
      String sessionId = ((RemoteWebDriver) webDriver).getSessionId().toString();
      String nodeIp = TestUtils.getPrivateIp(this.instrumentation.getRemoteAddress(), sessionId);
      result.append("client ").append(nodeIp);
      if (((RemoteWebDriver) webDriver).getCapabilities().getPlatform().toString().equalsIgnoreCase("LINUX")) {
        result.append(this.runCommandClient(this.command, this.instrumentation, nodeIp));
      } else {
        result.append("Node ").append(nodeIp).append(" is not Linux");
      }
    }
    return result.toString();
  }

  public String cleanUp(WebDriver webDriver) {
    StringBuilder result = new StringBuilder();
    String cleanUpCommand = "sudo tc qdisc del dev " + this.nit + " root || true && sudo tc qdisc del dev " + this.nit + " ingress || true && sudo tc qdisc del dev ifb0 root ||true ";
    result.append("Doing CleanUp for ").append(this.command).append( " on ");
    if ( this.type.equals("gateway")) {
      result.append(this.gateway);
      logger.info(result.toString());
      result.append(this.runCommandGateway(cleanUpCommand, this.instrumentation));
    }
    if ( this.type.equals("client")) {
      String sessionId = ((RemoteWebDriver) webDriver).getSessionId().toString();
      String nodeIp = TestUtils.getPrivateIp(this.instrumentation.getRemoteAddress(), sessionId);
      result.append("client ").append(nodeIp);
      if (((RemoteWebDriver) webDriver).getCapabilities().getPlatform().toString().equalsIgnoreCase("LINUX")) {
        result.append(this.runCommandClient(cleanUpCommand, this.instrumentation, nodeIp));
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

  private String KiteServerCommand(String url) {
    String result;
    try {
      URLConnection connection = new URL(url).openConnection();
      connection.setRequestProperty("Accept-Charset", "UTF-8");
      InputStream response = connection.getInputStream();
      result = "SUCCEEDED and got response : " + response.toString();
    } catch (Exception e) {
      result = "thrown ERROR : " + e.getLocalizedMessage();
    }
    return result;
  }

  private String runCommandGateway(String command, Instrumentation instrumentation) {
    StringBuilder result = new StringBuilder();
    String instrumentUrl = instrumentation.getInstrumentUrl();
    String GridId = instrumentation.getKiteServerGridId();
    if (instrumentUrl == null) {
      String url = "http://localhost:8080/KITEServer/command?id=" + GridId + "&gw=" + this.gateway + "&command=" + command;
      result.append("via KiteServer ").append(this.KiteServerCommand(url));
    } else if (instrumentUrl.endsWith(".json")) {
      Instance instance = instrumentation.get(this.gateway);
      result.append(" via ssh ").append(this.sshCommand(instance, command));
    } else if (instrumentUrl.startsWith("http://")) {
      String url = instrumentUrl + GridId + "&gw=" + this.gateway + "&command=" + command;
      result.append("via KiteServer ").append(this.KiteServerCommand(url));
    } else {
      result.append(" failed, check instrumentUrl !");
    }
    return result.toString();
  }

  private String runCommandClient(String command, Instrumentation instrumentation, String nodeIp) {
    StringBuilder result = new StringBuilder();
    String instrumentUrl = instrumentation.getInstrumentUrl();
    String GridId = instrumentation.getKiteServerGridId();
    if (instrumentUrl == null) {
      String url = "http://localhost:8080/KITEServer/command?id=" + GridId + "&ip=" + nodeIp + "&command=" + command;
      result.append(this.KiteServerCommand(url));
    } else if (instrumentUrl.startsWith("http://")) {
      String url = instrumentUrl + GridId + "&ip=" + nodeIp + "&command=" + command;
      result.append("via KiteServer ").append(this.KiteServerCommand(url));
    } else {
      result.append(" FAILED, check instrumentUrl !");
    }
    return result.toString();
  }

  public boolean shouldExecute(int clientId) {
    return (this.getType().equals("gateway") && clientId == 0) || (this.getType().equals("client") && this.getClientIds().contains(clientId));
  }
}
