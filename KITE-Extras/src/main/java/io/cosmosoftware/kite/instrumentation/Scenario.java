/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.instrumentation;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.manager.SSHManager;
import io.cosmosoftware.kite.report.Status;
import io.cosmosoftware.kite.util.ReportUtils;
import org.apache.log4j.Logger;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.HashMap;

public class Scenario {

  private final Instrumentation instrumentation;
  private final int clientId;
  private final  String name;
  private HashMap<String, String> commandList = new HashMap<>();
  private final int duration;
  private final Logger logger;

  public Scenario(JsonObject jsonObject, Logger logger, int i, Instrumentation instrumentation) throws Exception {

    this.instrumentation = instrumentation;
    this.clientId = jsonObject.containsKey("clientId") ? jsonObject.getInt("clientId") : 0;
    if (clientId < 0) {
      throw new Exception(" Error in json config scenario, clientId specified is invalid ! ");
    }
    for (String gw  : this.instrumentation.keySet()) {
      commandList.put(gw, "");
    }
    String missingKey="";
    String gateway;
    String command;
    try {
      missingKey = "commandList";
      JsonArray jsonArray = jsonObject.getJsonArray("commandList");
      for (int j = 0 ; j < jsonArray.size() ; j++) {
        JsonObject jsonObject2 = jsonArray.getJsonObject(j);
        NWCommands nwCommands = new NWCommands(jsonObject2, this.instrumentation);
        gateway = nwCommands.getGateway();
        command = this.commandList.get(gateway);
        this.commandList.remove(gateway);
        if (!command.equals("")) {
          command += "|| true && " + nwCommands.getCommand();
        } else {
          command += nwCommands.getCommand();
        }
        commandList.put(gateway, command);
      }
    } catch (NullPointerException e) {
      throw new KiteTestException("Error in json config scenario, the key " + missingKey + " is missing.", Status.FAILED, e);
    }
    name = jsonObject.getString("name", "Scenario number : " + i);
    duration = jsonObject.containsKey("duration") ? jsonObject.getInt("duration") : 10000;
    this.logger = logger;
  }

  public String getName() {
    return name;
  }

  public int getClientId() {
    return clientId;
  }

  public HashMap<String, String> getCommandList() {
    return commandList;
  }

  public Integer getDuration() {
    return duration;
  }

  public String runCommands() {
    StringBuilder result = new StringBuilder();
    for (String gw : this.commandList.keySet()) {
      String command = this.commandList.get(gw);
      if (!command.equals("")) {
        logger.info("Trying to run " + command + "on " + gw);
        Instance instance = instrumentation.get(gw);
        result.append(command);
        logger.info("Executing command : " + command + "on " + instance.getIpAddress());
        try {
          SSHManager sshManager = new SSHManager(instance.getKeyFilePath(), instance.getUsername(),
              instance.getIpAddress(), command);
          if (sshManager.call().commandSuccessful()) {
            Thread.sleep(1000);
            logger.info("runCommands() : \r\n" + command);
            result.append("  SUCCESS (Client : ").append(instance.getIpAddress()).append(")");
          } else {
            logger.error("Failed runCommands() : \r\n" + command);
            result.append("  FAILURE (Client : ").append(instance.getIpAddress()).append(")");
          }
        } catch (Exception e) {
          logger.error(
              "runCommand(). Command:\r\n"
                  + command
                  + "\r\n"
                  + ReportUtils.getStackTrace(e));
          result.append("  Error ").append(e.getMessage());
        }
        result.append("\n\n");
      }
    }
    return result.toString();
  }

  public String cleanUp() {
    StringBuilder result = new StringBuilder();
    for (String gw : this.commandList.keySet()) {
      String command = this.commandList.get(gw);
      if (!command.equals("")) {
        Instance instance = instrumentation.get(gw);
        String[] interfacesList = {instance.getNit0(), instance.getNit1(), instance.getNit2()};
        for (String inter : interfacesList) {
          if (command.contains(inter)) {
            String cleanUpCommand = "sudo tc qdisc del dev " + inter + " root";
            try {
              SSHManager sshManager = new SSHManager(instance.getKeyFilePath(), instance.getUsername(),
                  instance.getIpAddress(), cleanUpCommand);
              if (sshManager.call().commandSuccessful()) {
                Thread.sleep(1000);
                logger.info("cleanUp() : " + inter + " on gateway " + gw);
                result.append(cleanUpCommand);
              } else {
                logger.error("Failed cleanUp() : " + inter + " on gateway " + gw);
                result.append("  FAILURE (Client : ").append(instance.getIpAddress()).append(")");
              }
            } catch (Exception e) {
              logger.error(
                  "cleanUp(). \r\n"
                      + inter
                      + "\r\n"
                      + ReportUtils.getStackTrace(e));
              result.append("  Error ").append(e.getMessage());
            }
            result.append(" on gateway ").append(gw).append("\n\n");
          } else {
            logger.info("No CleanUp to do on interface : " + inter + " on gateway " + gw);
          }
        }
      }
    }
    return result.toString();

  }


}
