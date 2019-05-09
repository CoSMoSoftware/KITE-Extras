/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.instrumentation;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.report.Status;

import javax.json.JsonObject;

public class NWCommands {

  private final String nit;
  private final String gateway;
  private final String finalCommand;
  private final int delay;
  private final int packetloss;
  private final int corrupt;
  private final int duplicate;
  private final JsonObject bandwidth;
  private final Instrumentation instrumentation;

  public NWCommands(JsonObject jsonObject, Instrumentation instrumentation) throws Exception {
    try {
      this.instrumentation = instrumentation;
      this.gateway = jsonObject.getString("gateway");
      if (instrumentation.get(gateway) == null) {
        throw new Exception(" Error in json config scenario, gateway specified is not in the instrumentation file ! ");
      }
    } catch (NullPointerException e) {
      throw new KiteTestException("Error in json config scenario, the key gateway is missing.", Status.BROKEN, e);
    }
    this.nit = jsonObject.containsKey("nit") && !jsonObject.containsKey("command") ? jsonObject.getString("nit") : null;
    this.finalCommand = jsonObject.containsKey("command") ? jsonObject.getString("command") : null;
    if (this.finalCommand == null && this.nit == null) {
      throw new KiteTestException("Error in json config scenario, the key interface is missing.", Status.BROKEN);
    }
    this.delay =  jsonObject.getInt("delay", 0);
    this.packetloss = jsonObject.getInt("packetloss", 0);
    this.corrupt = jsonObject.getInt("corrupt", 0);
    this.duplicate = jsonObject.getInt("duplicate", 0);
    this.bandwidth = jsonObject.containsKey("bandwidth") ? jsonObject.getJsonObject("bandwidth") : null;
  }

  public String getGateway() {
    return this.gateway;
  }

  public String getCommand() throws Exception {
    String command = "";
    if (this.finalCommand != null) {
      command = this.finalCommand;
    } else {
      if (this.delay != 0) {
        command = createCommand(command, "delay " + delay + "ms ");
      }
      if (this.packetloss != 0) {
        command = createCommand(command, "loss " + packetloss + "% ");
      }
      if (this.corrupt != 0) {
        command = createCommand(command, "corrupt " + corrupt + "% ");
      }
      if (this.duplicate != 0) {
        command = createCommand(command, "duplicate " + duplicate + "% ");
      }
      if (this.bandwidth != null) {
        try {
          if (command.equals("")) {
            command = "sudo tc qdisc add dev " + nit + " root tbf rate " + this.bandwidth.getInt("rate") + "kbit burst " + this.bandwidth.getInt("burst") + "kb latency " + this.bandwidth.getInt("latency") + "ms ";
          } else {
            command += "|| true && sudo tc qdisc add dev " + nit + " parent 1: tbf rate " + this.bandwidth.getInt("rate") + "kbit burst " + this.bandwidth.getInt("burst") + "kb latency " + this.bandwidth.getInt("latency") + "ms ";
          }
        } catch (NullPointerException e) {
          throw new KiteTestException("Parameters are missing in bandwidth command.", Status.FAILED, e);
        }
      }
    }
    if (command.equals("")) {
      throw new KiteTestException("No command to run.", Status.BROKEN);
    }
    return command;
  }

  private String createCommand(String command, String info) {
    if (command.equals("")) {
      command = "sudo tc qdisc add dev " + nit + " root handle 1: netem " + info;
    } else {
      command += info;
    }
    return command;
  }
}
