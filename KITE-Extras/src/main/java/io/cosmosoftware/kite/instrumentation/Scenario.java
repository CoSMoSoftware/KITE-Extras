package io.cosmosoftware.kite.instrumentation;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.manager.SSHManager;
import io.cosmosoftware.kite.report.Status;
import io.cosmosoftware.kite.util.ReportUtils;
import org.apache.log4j.Logger;

import javax.json.JsonObject;

public class Scenario {

  private final Instrumentation instrumentation;
  private final int clientId;
  private final  String name;
  private final String gateway;
  private final String command;
  private final int duration;
  private final Logger logger;


  public Scenario(JsonObject jsonObject, Logger logger, int i, Instrumentation instrumentation) throws Exception {

    this.instrumentation = instrumentation;
    clientId = jsonObject.containsKey("clientId") ? jsonObject.getInt("clientId") : 0;
    if (clientId < 0) {
      throw new Exception(" Error in json config scenario, clientId specified is invalid ! ");
    }
    String missingKey="";
    try {
      missingKey = "gateway";
      gateway = jsonObject.getString("gateway");
      missingKey = "command";
      command = jsonObject.getString("command");
      if (instrumentation.get(gateway) == null) {
        throw new Exception(" Error in json config scenario, gateway specified is not in the instrumentation file ! ");
      }
    } catch (NullPointerException e) {
      throw new KiteTestException("Error in json config scenario, the key " + missingKey + " is missing.", Status.BROKEN, e);
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

  public String getGateway() {
    return gateway;
  }

  public String getCommand() {
    return command;
  }

  public Integer getDuration() {
    return duration;
  }

  public String runCommands() {
    logger.info("Trying to run " + this.command + " on " + this.gateway);
    Instance instance = instrumentation.get(this.gateway);
    String result = this.command;
    logger.info("Executing command : " + command + " on " + instance.getIpAddress());
    try {
      SSHManager sshManager = new SSHManager(instance.getKeyFilePath(), instance.getUsername(),
          instance.getIpAddress(), command);
      if (sshManager.call().commandSuccessful()) {
        Thread.sleep(this.duration);
        logger.info("runCommands() : \r\n" + command);
        result += "  SUCCESS (Client : " + instance.getIpAddress() + ")";
      } else {
        logger.error("Failed runCommands() : \r\n" + this.command);
        result += "  FAILURE (Client : " + instance.getIpAddress() + ")";
      }
    } catch (Exception e) {
      logger.error(
          "runCommand(). Command:\r\n"
              + command
              + "\r\n"
              + ReportUtils.getStackTrace(e));
      result += "  Error " + e.getMessage();
    }
    return result;
  }

  public String cleanUp() {
    String result = "";
    Instance instance = instrumentation.get(this.gateway);
    String[]  interfacesList = {instance.getNit0(), instance.getNit1(), instance.getNit2()};
    for (String inter : interfacesList) {
      if (this.command.contains(inter)) {
        String cleanUpCommand = "sudo tc qdisc del dev " + inter + " root";
        try {
          SSHManager sshManager = new SSHManager(instance.getKeyFilePath(), instance.getUsername(),
              instance.getIpAddress(), cleanUpCommand);
          if (sshManager.call().commandSuccessful()) {
            Thread.sleep(1000);
            logger.info("cleanUp() : " + inter);
            result += cleanUpCommand;
          } else {
            logger.error("Failed cleanUp() : " + inter);
            result += "  FAILURE (Client : " + instance.getIpAddress() + ")";
          }
        } catch (Exception e) {
          logger.error(
              "cleanUp(). \r\n"
                  + inter
                  + "\r\n"
                  + ReportUtils.getStackTrace(e));
          result += "  Error " + e.getMessage();
        }
      }
      else {
        logger.info("No CleanUp to do on interface : " + inter);
      }
    }
    return result;

  }


}
