/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.instrumentation;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.report.Status;

import javax.json.JsonObject;

public class Profile {

  private final String nit;
  private final String command;
  private final int delay;
  private final int packetloss;
  private final int corrupt;
  private final int duplicate;
  private final int bandwidth;
  private static final String INTERFACE_0_NAME = "enp0s8";
  private static final String INTERFACE_1_NAME = "enp0s9";

  public Profile(JsonObject jsonObject) throws Exception {
    this.delay =  jsonObject.getInt("delay", 0);
    this.packetloss = jsonObject.getInt("packetloss", 0);
    this.corrupt = jsonObject.getInt("corrupt", 0);
    this.duplicate = jsonObject.getInt("duplicate", 0);
    this.bandwidth = jsonObject.getInt("bandwidth", 0);
    this.command = jsonObject.containsKey("command") ? jsonObject.getString("command") : this.setCommand();
    this.nit = this.command.contains(INTERFACE_0_NAME) ? INTERFACE_0_NAME : INTERFACE_1_NAME;
  }

  private String setCommand() throws Exception {
    String command;
    String egress_command = ""; // command for traffic going out
    String ingress_command = ""; // command for traffic going in
    command = "sudo ip link add ifb0 type ifb || true && sudo ip link set up dev ifb0 || true && sudo tc qdisc add dev " + INTERFACE_0_NAME + " ingress || true && sudo tc filter add dev " + INTERFACE_0_NAME + " parent ffff: protocol ip u32 match u32 0 0 action mirred egress redirect dev ifb0 || true && ";
    if (this.delay != 0) {
      egress_command = createCommand(egress_command, "delay " + delay + "ms ", INTERFACE_0_NAME);
      ingress_command = createCommand(ingress_command, "delay " + delay + "ms ", "ifb0");
    }
    if (this.packetloss != 0) {
      egress_command = createCommand(egress_command, "loss " + packetloss + "% ", INTERFACE_0_NAME);
      ingress_command = createCommand(ingress_command, "loss " + packetloss + "% ", "ifb0");
    }
    if (this.corrupt != 0) {
      egress_command = createCommand(egress_command, "corrupt " + corrupt + "% ", INTERFACE_0_NAME);
      ingress_command = createCommand(ingress_command, "corrupt " + corrupt + "% ", "ifb0");
    }
    if (this.duplicate != 0) {
      egress_command = createCommand(egress_command, "duplicate " + duplicate + "% ", INTERFACE_0_NAME);
      ingress_command = createCommand(ingress_command, "duplicate " + duplicate + "% ", "ifb0");
    }
    if (this.bandwidth != 0) {
      egress_command = createCommand(egress_command, "rate " + bandwidth + "kbit ", INTERFACE_0_NAME);
      ingress_command = createCommand(ingress_command, "rate " + bandwidth + "kbit ", "ifb0");
    }
    command += egress_command + "|| true && " + ingress_command;
    if (egress_command.equals("") && ingress_command.equals("")) {
      throw new KiteTestException("No command to run.", Status.BROKEN);
    }
    return command;
  }

  public String getCommand() {
    return this.command;
  }

  public String getInterface() {
    return this.nit;
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
