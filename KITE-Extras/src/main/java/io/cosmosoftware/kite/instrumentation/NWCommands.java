/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.instrumentation;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.report.Status;

import javax.json.JsonObject;

public class NWCommands {

  private final String nit;
  private final String finalCommand;
  private final int delay;
  private final int packetloss;
  private final int corrupt;
  private final int duplicate;
  private final int bandwidth;

  public NWCommands(JsonObject jsonObject) throws Exception {
    this.nit = "enp0s8";
    this.finalCommand = jsonObject.containsKey("command") ? jsonObject.getString("command") : null;
    this.delay =  jsonObject.getInt("delay", 0);
    this.packetloss = jsonObject.getInt("packetloss", 0);
    this.corrupt = jsonObject.getInt("corrupt", 0);
    this.duplicate = jsonObject.getInt("duplicate", 0);
    this.bandwidth = jsonObject.getInt("bandwidth", 0);
  }

  public String getCommand() throws Exception {
    String command;
    String egress_command = "";
    String ingress_command = "";
    if (this.finalCommand != null) {
      command = this.finalCommand;
    } else {
      command = "sudo ip link add ifb0 type ifb || true && sudo ip link set up dev ifb0 || true && sudo tc qdisc add dev " + this.nit + " ingress || true && sudo tc filter add dev " + this.nit + " parent ffff: protocol ip u32 match u32 0 0 action mirred egress redirect dev ifb0 || true && ";
      if (this.delay != 0) {
        egress_command = createCommand(egress_command, "delay " + delay + "ms ", this.nit);
        ingress_command = createCommand(ingress_command, "delay " + delay + "ms ", "ifb0");
      }
      if (this.packetloss != 0) {
        egress_command = createCommand(egress_command, "loss " + packetloss + "% ", this.nit);
        ingress_command = createCommand(ingress_command, "loss " + packetloss + "% ", "ifb0");
      }
      if (this.corrupt != 0) {
        egress_command = createCommand(egress_command, "corrupt " + corrupt + "% ", this.nit);
        ingress_command = createCommand(ingress_command, "corrupt " + corrupt + "% ", "ifb0");
      }
      if (this.duplicate != 0) {
        egress_command = createCommand(egress_command, "duplicate " + duplicate + "% ", this.nit);
        ingress_command = createCommand(ingress_command, "duplicate " + duplicate + "% ", "ifb0");
      }
      if (this.bandwidth != 0) {
        egress_command = createCommand(egress_command, "rate " + bandwidth + "kbit ", this.nit);
        ingress_command = createCommand(ingress_command, "rate " + bandwidth + "kbit ", "ifb0");
      }
      command += egress_command + "|| true && " + ingress_command;
    }
    if (command.equals("")) {
      throw new KiteTestException("No command to run.", Status.BROKEN);
    }
    return command;
  }

  private String createCommand(String command, String info, String nit) {
    if (command.equals("")) {
      command = "sudo tc qdisc replace dev " + nit + " root netem " + info;
    } else {
      command += info;
    }
    return command;
  }
}
