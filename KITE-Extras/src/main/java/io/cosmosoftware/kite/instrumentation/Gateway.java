/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.instrumentation;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.manager.SSHManager;
import io.cosmosoftware.kite.report.Status;
import io.cosmosoftware.kite.util.ReportUtils;
import org.apache.log4j.Logger;

import javax.json.JsonObject;

/**
 * The type Gateway.
 */
public class Gateway extends InstanceBase {
  
  private static final Logger logger = Logger.getLogger(Gateway.class.getName());
  private final String eth0IP;
  private final String eth1IP;
  private final String publicIP;
  
  
  /**
   * Constructor builds the Gateway object from a json:
   * <p>
   * "gateways": [
   * {
   * "publicIP": "174.129.187.140",
   * "eth0IP": "172.31.12.174",
   * "eth1IP": "172.31.4.13",
   * "username": "ec2-user",
   * "keyFilePath": "/.ssh/CosmoKeyUS.pem",
   * "commands": [
   * "sudo tc qdisc add dev ens3 root netem loss 5%"
   * ]
   * }
   * ]
   *
   * @param jsonObject the json object
   *
   * @throws Exception the exception
   */
  public Gateway(JsonObject jsonObject) throws Exception {
    super(jsonObject);
    publicIP = jsonObject.getString("publicIP");
    eth0IP = jsonObject.getString("eth0IP");
    eth1IP = jsonObject.getString("eth1IP");
  }
  
  /**
   * Add a static route on the gateway on eth1 to the ip given as param
   *
   * @param ip the ip to add the static route to.
   */
  public void addStaticRoute(String ip, String eth) {
    String command = "sudo route add -host " + ip + " " + eth;
    try {
      SSHManager sshManager = new SSHManager(this.keyFilePath, this.username,
        this.publicIP, command);
      if (sshManager.call().commandSuccessful()) {
        Thread.sleep(500);
        logger.info("static route to " + ip + " via " + eth + " added.");
      } else {
        logger.error("Failed to add static route to " + ip + " via "+ eth);
      }
    } catch (Exception e) {
      logger.error("Error in Gateway.addStaticRouteOnGateway. Command:\r\n"
        + command + "\r\n"
        + ReportUtils.getStackTrace(e));
    }
  }
  
  /**
   * Gets the eth1IP
   *
   * @return the eth1IP
   */
  public String getEth1IP() {
    return this.eth1IP;
  }
  
  /**
   * Runs the tc commands on the gateway.
   *
   * @return the command that has been run.
   */
  public String runCommands(boolean cleanUp) throws KiteTestException {
    String command = cleanUp ? getCleanUpCommandLine() : getCommandLine();
    if (command == null) {
      logger.warn("runCommands(cleanUp = " + cleanUp +" ) no command to run");
      return "";
    }
    if ( (!cleanUp && commandExecuted) || (cleanUp && !commandExecuted)) {
      logger.warn("runCommands(cleanUp = " + cleanUp + " ) was already executed.");
      return "";
    }
    try {
      SSHManager sshManager = new SSHManager(this.keyFilePath, this.username,
        this.publicIP, command);
      if (sshManager.call().commandSuccessful()) {
        Thread.sleep(1500);
        logger.info("runCommands(cleanUp = " + cleanUp + " ) : \r\n" + command);
        command += "  SUCCESS (on Gateway)";
      } else {
        logger.error("Failed runCommands(cleanUp = " + cleanUp + " ) : \r\n" + command);
        command += "  FAILURE (on Gateway)";
        throw new KiteTestException(command, Status.FAILED);
      }
    } catch (KiteTestException e) {
      throw  e;
    } catch (Exception e) {
      logger.error(
          "runCommand(cleanUp = " + cleanUp + " ). Command:\r\n"
              + command
              + "\r\n"
              + ReportUtils.getStackTrace(e));
      command = "FAILURE: error " + e.getMessage();
      throw new KiteTestException(command, Status.BROKEN);
    }
    commandExecuted = !cleanUp;
    return command;
  }
  
  /**
   * For debugging.
   *
   * @return a String representation of this object
   */
  @Override
  public String toString() {
    String s = "\r\n";
    s += "publicIP: " + this.publicIP + "\r\n";
    s += "eth0IP: " + this.eth0IP + "\r\n";
    s += "eth1IP: " + this.eth1IP + "\r\n";
    s += super.toString();
    return s;
  }
}
