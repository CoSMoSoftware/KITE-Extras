/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.instrumentation;

import io.cosmosoftware.kite.exception.KiteTestException;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.Vector;


/**
 * Network instrumentation config helper class.
 * <p>
 * Example:
 * <p>
 * "instrumentation": {
 * "phase": "rampup",
 * "allClients": false,
 * "serverIPs": ["18.215.197.60", "18.213.125.68"],
 * "gateway": {
 * "publicIP": "174.129.187.140",
 * "eth0IP": "172.31.12.174",
 * "eth1IP": "172.31.4.13",
 * "username": "ec2-user",
 * "keyFilePath": "/.ssh/CosmoKeyUS.pem",
 * "cleanUp": true,
 * "commands": [
 * "sudo tc qdisc add dev ens3 root netem loss 5%"
 * ]
 * },
 * "clients": [
 * {
 * "id": 1,
 * "username": "ubuntu",
 * "keyFilePath": "/.ssh/CosmoKeyUS.pem",
 * "commands": [
 * "sudo tc qdisc add dev ens3 handle ffff: ingress || true && sudo tc filter add dev ens3 parent ffff: protocol ip prio 50 u32 match ip src 18.215.197.60 police rate 640kbit burst 10k drop flowid :1 || true && sudo tc filter add dev ens3 parent ffff: protocol ip prio 50 u32 match ip src 18.213.125.68 police rate 640kbit burst 10k drop flowid :1"
 * ]
 * }
 * ],
 * <p>
 * Default value: phase="rampup", allClients=false.
 * gateway is optional (NW instrumentation can be done on the clients only).
 * clients cannot be empty.
 * all fields in client and gateway are mandatory.
 * serverIPs is mandatory when a gateway is specified.
 * <p>
 * <p>
 * phase:  rampup, loadreached, both (case insensitive)
 */
public class NWInstConfig {
  
  private final Vector<Client> clients = new Vector<>();
  private final Vector<Gateway> gateways = new Vector<>();
  private final boolean gatewaySetup;
  private final Vector<String> serverIPs = new Vector<>();
  private boolean allClients = false;

  private String phase = "rampup"; //rampup | loadreached | both
  
  
  /**
   * Instantiates a new Nw inst config.
   *
   * @param jsonPayload the json payload
   *
   * @throws Exception the exception
   */
  public NWInstConfig(JsonObject jsonPayload) throws Exception {
    phase = jsonPayload.getString("phase", phase);
    JsonArray jsonArray = jsonPayload.getJsonArray("gateways");
    if (jsonArray != null) {
      for (int i = 0; i < jsonArray.size(); i++) {
        gateways.add(new Gateway(jsonArray.getJsonObject(i)));
      }
    }
    
    jsonArray = jsonPayload.getJsonArray("clients");
    for (int i = 0; i < jsonArray.size(); i++) {
      clients.add(new Client(jsonArray.getJsonObject(i)));
    }
    jsonArray = jsonPayload.getJsonArray("serverIPs");
    if (jsonArray != null) {
      for (int i = 0; i < jsonArray.size(); i++) {
        serverIPs.add(jsonArray.getString(i));
      }
    }
    gatewaySetup = gateways.size() > 0;
  }
  
  /**
   * Adds the static routes on the client node VM to route the traffic to
   * the server IPs via the gateway.
   *
   * @param id     the id
   * @param nodeIp the node ip
   */
  public void addStaticRouteOnClient(int id, int gwIndex, String nodeIp) {
    getClient(id).addStaticRoutes(nodeIp, serverIPs, gateways.elementAt(gwIndex).getEth1IP());
  }
  
  /**
   * Adding a static route to the client private IP on all the gateways.
   *
   * @param ip the ip of the client
   * @param gwIndex the index of the gateway in the list
   * @param eth name of the network interface on the gateway
   */
  public void addStaticRouteOnGateway(String ip, int gwIndex, String eth) {
    gateways.elementAt(gwIndex).addStaticRoute(ip, eth);
  }
  
  /**
   * Runs the cleanUp commands on the gateway.
   */
  public void cleanUp() throws KiteTestException {
    for (Gateway gw : gateways) {
      gw.runCommands(true);
    }
  }
  
  /**
   * Checks if the tester ID is included in the list of clients to be tested.
   * In the json
   *
   * @param id the id of the client
   *
   * @return true if this tester ID is included in the config file, or if the config is set to [0]
   */
  public boolean contains(int id) {
    if (this.allClients) {
      return true;
    }
    for (Client c : clients) {
      if (id == c.getId()) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Gateway setup boolean.
   *
   * @return true if this is a gateway setup, false otherwise.
   */
  public boolean gatewaySetup() {
    return gatewaySetup;
  }
  
  /**
   * Gets the client corresponding to the id.
   *
   * @param id the id
   *
   * @return the client corresponding to this id
   */
  public Client getClient(int id) {
    for (Client c : clients) {
      if (id == c.getId()) {
        return c;
      }
    }
    return null;
  }
  
  /**
   * Checks if the tester ID is included in the list of clients to be tested and if the
   * NW instrumentation should be done during load reached.
   *
   * @param id the id of the client
   *
   * @return true if the NW instrumentation is to be executed during the load reached phase and this id is included in the list provided in the config file
   */
  public boolean loadreached(int id) {
    return "both".equals(this.phase.toLowerCase()) || "loadreached".equals(this.phase.toLowerCase())
      && this.contains(id);
  }

  
  /**
   * Checks if the tester ID is included in the list of clients to be tested and if the
   * NW instrumentation should be done during ramp up.
   *
   * @param id the id of the client
   *
   * @return true if the NW instrumentation is to be executed during the ramp up phase and this id is included in the list provided in the config file
   */
  public boolean rampup(int id) {
    return "both".equals(this.phase.toLowerCase()) || "rampup".equals(this.phase.toLowerCase())
      && this.contains(id);
  }


  /**
   * Runs the NW instrumentation commands on the gateway.
   *
   * @param id     the id
   * @param nodeIP the node ip
   *
   * @return the string
   */
  public String runCommands(int id, int gwIndex, String nodeIP) throws KiteTestException {
    if (gatewaySetup) {
      return gateways.elementAt(gwIndex).runCommands(false);
    } else {
      return getClient(id).runCommands(nodeIP);
    }
  }



  /**
   * Runs the NW instrumentation commands on the gateway.
   *
   * @param id     the gateway id/index
   *
   * @return the command that has been run.
   */
  public String runGatewayCommands(int id) throws KiteTestException {
    return gateways.elementAt(id).runCommands(false);
  }



  /**
   * Runs the NW instrumentation commands on the node.
   *
   * @param id     the node id/index
   * @param nodeIP the IP of the node to run the command to.
   *
   * @return the command that has been run.
   */
  public String runNodeCommands(int id, String nodeIP) {
    return getClient(id).runCommands(nodeIP);
  }

  /**
   * For debugging.
   *
   * @return a String representation of this object
   */
  @Override
  public String toString() {
    String s = "\r\n";
    s += "Phase: " + this.phase + "\r\n";
    s += "allClients: " + this.allClients + "\r\n";
    s += "gateways: " + "\r\n";
    for (Gateway g : gateways) {
      s += " " + g.toString() + "\r\n";
    }
    s += "clients: ";
    for (Client c : clients) {
      s += " " + c.toString() + "\r\n";
    }
    return s;
  }
  
  
}
