/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.instrumentation;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.manager.SSHManager;
import io.cosmosoftware.kite.report.Status;
import io.cosmosoftware.kite.util.ReportUtils;
import io.cosmosoftware.kite.util.TestUtils;
import net.bytebuddy.implementation.bytecode.Throw;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.awt.*;
import java.net.URI;
import java.util.ArrayList;

import static io.cosmosoftware.kite.util.TestUtils.getPrivateIp;

public class Scenario {

  private final String type;
  private final String name;
  private final String command;
  private final String network;
  private final String gateway;
  private ArrayList<Integer> clientIds = new ArrayList<>();
  private final Networks networks;
  private final Logger logger;

  public Scenario(JsonObject jsonObject, Logger logger, Networks networks) throws Exception {
    this.logger = logger;
    String missingKey="";
    this.networks = networks;
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
      this.command =  this.networks.get(this.network).getProfile().getCommand();
      missingKey = "name";
      name = jsonObject.getString("name");
    } catch (Exception e) {
      throw new KiteTestException("The key " + missingKey + " is missing", Status.FAILED, e);
    }
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

  public String sendCommand(WebDriver webDriver, Instrumentation instrumentation, String remoteAddress, String GridId, String instrumentUrl) {
    StringBuilder result = new StringBuilder();
    result.append("Running ").append(this.command).append("on ");
    if (this.type.equals("gateway")) {
      result.append(this.gateway);
      logger.info(result.toString());
      if (instrumentUrl == null) {
        String url = "http://localhost:8080/KITEServer/command?id=" + GridId + "&gw=" + this.gateway + "&command=" + this.command;
        result.append("via KiteServer ").append(KiteServerCommand(url));
      } else if (instrumentUrl.endsWith(".json")) {
        Instance instance = instrumentation.get(gateway);
        result.append(" via ssh ").append(sshCommand(instance, command));
      } else if (instrumentUrl.startsWith("http://")) {
        String url = instrumentUrl + GridId + "&gw=" + this.gateway + "&command=" + this.command;
        result.append("via KiteServer ").append(KiteServerCommand(url));
      } else {
        result.append(" failed, check instrumentUrl !");
      }
    }
    if (this.type.equals("client")) {
      logger.info("!!! HUB IP : " + remoteAddress + " !!!");
      String sessionId = ((RemoteWebDriver) webDriver).getSessionId().toString();
      String nodeIp = TestUtils.getPrivateIp(remoteAddress, sessionId);
      result.append("client ").append(nodeIp);
      if (((RemoteWebDriver) webDriver).getCapabilities().getPlatform().toString().equalsIgnoreCase("LINUX")) {
        if (instrumentUrl == null) {
          String url = "http://localhost:8080/KITEServer/command?id=" + GridId + "&ip=" + nodeIp + "&command=" + this.command;
          result.append(KiteServerCommand(url));
        } else if (instrumentUrl.startsWith("http://")) {
          String url = instrumentUrl + GridId + "&ip=" + nodeIp + "&command=" + this.command;
          result.append("via KiteServer ").append(KiteServerCommand(url));
        } else {
          result.append(" failed, check instrumentUrl !");
        }
      } else {
        result.append("Sorry node ").append(nodeIp).append(" is not Linux");
      }
    }
    return result.toString();
  }

  public String cleanUp(WebDriver webDriver, Instrumentation instrumentation, String remoteAddress, String GridId, String instrumentUrl) {
    StringBuilder result = new StringBuilder();
    String inter = "enp0s8";
    String cleanUpCommand = "sudo tc qdisc del dev " + inter + " root || true && sudo tc qdisc del dev " + inter + " ingress || true && sudo tc qdisc del dev ifb0 root ";
    result.append("Doing CleanUp for ").append(cleanUpCommand).append( "on ");
    if ( this.type.equals("gateway")) {
      result.append(this.gateway);
      logger.info(result.toString());
      if ( instrumentUrl == null ) {
        String url = "http://localhost:8080/KITEServer/command?id=" + GridId + "&gw=" + this.gateway + "&command=" + cleanUpCommand;
        result.append("via KiteServer ").append(KiteServerCommand(url));
      } else if (instrumentUrl.endsWith(".json")) {
        Instance instance = instrumentation.get(gateway);
        result.append(" via ssh ").append(sshCommand(instance, cleanUpCommand));
      } else if (instrumentUrl.startsWith("http://")) {
        String url = instrumentUrl + GridId + "&gw=" + this.gateway + "&command=" + cleanUpCommand;
        result.append("via KiteServer ").append(KiteServerCommand(url));
      } else {
        result.append(" FAILED, check instrumentUrl !");
      }
    }
    if ( this.type.equals("client")) {
      logger.info("!!! HUB IP : " + remoteAddress + " !!!");
      String sessionId = ((RemoteWebDriver) webDriver).getSessionId().toString();
      String nodeIp = TestUtils.getPrivateIp(remoteAddress, sessionId);
      result.append("client ").append(nodeIp);
      if (((RemoteWebDriver) webDriver).getCapabilities().getPlatform().toString().equalsIgnoreCase("LINUX")) {
        if ( instrumentUrl == null ) {
          String url = "http://localhost:8080/KITEServer/command?id=" + GridId + "&ip=" + nodeIp + "&command=" + cleanUpCommand;
          result.append(KiteServerCommand(url));
        } else if (instrumentUrl.startsWith("http://")) {
          String url = instrumentUrl + GridId + "&ip=" + nodeIp + "&command=" + cleanUpCommand;
          result.append("via KiteServer ").append(KiteServerCommand(url));
        } else {
          result.append(" FAILED, check instrumentUrl !");
        }
      } else {
        result.append("Sorry node ").append(nodeIp).append(" is not Linux");
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
      result = "thrown an ERROR";
    }
    return result;
  }

  private String KiteServerCommand(String url) {
    String result;
    try {
      Desktop desktop = java.awt.Desktop.getDesktop();
      URI oURL = new URI(url);
      desktop.browse(oURL);
      result = "SUCCEEDED";
    } catch (Exception e) {
      result = "thrown an ERROR";
    }
    return result;
  }

}
