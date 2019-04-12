/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.instrumentation;

import io.cosmosoftware.kite.manager.SSHManager;
import io.cosmosoftware.kite.util.ReportUtils;
import org.apache.log4j.Logger;

import javax.json.JsonObject;
import java.util.Vector;

/**
 * The type Client.
 */
public class Client extends InstanceBase {
  
  private static final Logger logger = Logger.getLogger(Gateway.class.getName());
  
  
  private final int id;
  
  
  /**
   * Constructor builds the Client object from a json object:
   * <p>
   * "clients": [
   * {
   * "id": 1,
   * "username": "ubuntu",
   * "keyFilePath": "/.ssh/CosmoKeyUS.pem",
   * "commands": [
   * "sudo tc qdisc add dev ens3 handle ffff: ingress || true && sudo tc filter add dev ens3 parent ffff: protocol ip prio 50 u32 match ip src 18.215.197.60 police rate 640kbit burst 10k drop flowid :1 || true && sudo tc filter add dev ens3 parent ffff: protocol ip prio 50 u32 match ip src 18.213.125.68 police rate 640kbit burst 10k drop flowid :1"
   * ]
   * }
   * ]
   *
   * @param jsonObject the json object
   *
   * @throws Exception the exception
   */
  public Client(JsonObject jsonObject) throws Exception {
    super(jsonObject);
    id = jsonObject.getInt("id");
  }
  
  /**
   * Add static route(s) on the client to the gateway eth1 ip adress for the list of
   * server IPs
   *
   * @param nodeIP         IP address of the node (client) where to add the static routes
   * @param destinationIps String array of the destination IPs
   * @param gatewayIp      the IP of the gateway where the traffic should be routed to.
   */
  public void addStaticRoutes(String nodeIP, Vector<String> destinationIps, String gatewayIp) {
    
    String command = "";
    for (int i = 0; i < destinationIps.size(); i++) {
      command += "sudo route add -host " + destinationIps.elementAt(i) + " gw " + gatewayIp;
      if (i < destinationIps.size() - 1) {
        command += pipe;
      }
    }
    try {
      SSHManager sshManager = new SSHManager(this.keyFilePath, this.username,
        nodeIP, command);
      if (sshManager.call().commandSuccessful()) {
        Thread.sleep(500);
        logger.info("static routes command executed successfully:\r\n" + command);
      } else {
        logger.error("Failed to step command:\r\n" + command);
      }
    } catch (Exception e) {
      logger.error("Error in Client.addStaticRoutes. Command:\r\n"
        + command + "\r\n"
        + ReportUtils.getStackTrace(e));
    }
  }
  
  /**
   * Get the id of this Client
   *
   * @return the id
   */
  public int getId() {
    return id;
  }
  
  /**
   * Runs the tc commands
   *
   * @param ip the ip
   *
   * @return the command that has been run.
   */
  public String runCommands(String ip) {
    String command = getCommandLine();
    try {
      SSHManager sshManager = new SSHManager(this.keyFilePath, this.username,
        ip, command);
      if (sshManager.call().commandSuccessful()) {
        Thread.sleep(1500);
        logger.info("runCommands() : \r\n" + command);
        command += "  SUCCESS (Client : " + ip + ")";
      } else {
        logger.error("Failed runCommands() : \r\n" + command);
        command += "  FAILURE (Client : " + ip + ")";
      }
    } catch (Exception e) {
      logger.error(
        "runCommand(). Command:\r\n"
          + command
          + "\r\n"
          + ReportUtils.getStackTrace(e));
      command = "Error " + e.getMessage();
    }
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
    s += "id: " + this.id + "\r\n";
    s += super.toString();
    return s;
  }
  
  
}
