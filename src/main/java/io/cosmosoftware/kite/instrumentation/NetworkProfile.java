/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.instrumentation;

import io.cosmosoftware.kite.config.KiteEntity;
import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.SampleData;
import io.cosmosoftware.kite.report.Status;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.json.JsonObject;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;


@Entity(name = NetworkProfile.TABLE_NAME)
public class NetworkProfile extends KiteEntity implements SampleData {

  final static String TABLE_NAME = "networkprofiles";
  private String id;
  private static final String INTERFACE_0_NAME = "eth9";
  private static final String INTERFACE_1_NAME = "enp0s9";
  private String nit;
  private String command;
  private int delay;
  private int packetloss;
  private int corrupt;
  private int duplicate;
  private int bandwidth;
  private String name;

  public NetworkProfile() {
    super();
  }

  public NetworkProfile(JsonObject jsonObject) throws Exception {
    this.delay =  jsonObject.getInt("delay", 0);
    this.packetloss = jsonObject.getInt("packetloss", 0);
    this.corrupt = jsonObject.getInt("corrupt", 0);
    this.duplicate = jsonObject.getInt("duplicate", 0);
    this.bandwidth = jsonObject.getInt("bandwidth", 0);
    this.command =
        jsonObject.containsKey("command") ? jsonObject.getString("command") : this.setCommand();
    this.nit = this.command.contains(INTERFACE_0_NAME) ? INTERFACE_0_NAME : INTERFACE_1_NAME;
    this.name = jsonObject.getString("name");
  }

  protected String setCommand() throws Exception {
    String command;
    String egress_command = ""; // command for traffic going out
    String ingress_command = ""; // command for traffic going in
    command =
        "sudo ip link add ifb0 type ifb || true && sudo ip link set up dev ifb0 || true && sudo tc qdisc add dev "
            + INTERFACE_0_NAME + " ingress || true && sudo tc filter add dev " + INTERFACE_0_NAME
            + " parent ffff: protocol ip u32 match u32 0 0 action mirred egress redirect dev ifb0 || true && ";
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
      egress_command = createCommand(egress_command, "duplicate " + duplicate + "% ",
          INTERFACE_0_NAME);
      ingress_command = createCommand(ingress_command, "duplicate " + duplicate + "% ", "ifb0");
    }
    if (this.bandwidth != 0) {
      egress_command = createCommand(egress_command, "rate " + bandwidth + "kbit ",
          INTERFACE_0_NAME);
      ingress_command = createCommand(ingress_command, "rate " + bandwidth + "kbit ", "ifb0");
    }
    command += egress_command + "|| true && " + ingress_command;
    if (egress_command.equals("") && ingress_command.equals("")) {
      throw new KiteTestException("No command to run.", Status.BROKEN);
    }
    this.command = command;
    return command;
  }
  
  public String getCommand() {
    return this.command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  @Transient
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

  /**
   * Gets id.
   *
   * @return the id
   */
  @Id
  @GeneratedValue(generator = NetworkProfile.TABLE_NAME)
  @GenericGenerator(name = NetworkProfile.TABLE_NAME, strategy = "io.cosmosoftware.kite.dao.KiteIdGenerator", parameters = {
      @Parameter(name = "prefix", value = "NWPR")})
  public String getId() {
    return this.id;
  }

  /**
   * Sets id.
   *
   * @param id the id
   */
  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }

  public int getBandwidth() {
    return bandwidth;
  }

  public void setBandwidth(int bandwidth) {
    this.bandwidth = bandwidth;
  }

  public int getCorrupt() {
    return corrupt;
  }

  public void setCorrupt(int corrupt) {
    this.corrupt = corrupt;
  }

  public int getDelay() {
    return delay;
  }

  public void setDelay(int delay) {
    this.delay = delay;
  }

  public int getDuplicate() {
    return duplicate;
  }

  public void setDuplicate(int duplicate) {
    this.duplicate = duplicate;
  }

  public int getPacketloss() {
    return packetloss;
  }

  public void setPacketloss(int packetloss) {
    this.packetloss = packetloss;
  }

  @Transient
  public String getNit() {
    return nit;
  }

  public void setNit(String nit) {
    this.nit = nit;
  }

  /**
   * Make sample data.
   *
   * @return the sample data
   */
  /*
   * (non-Javadoc)
   *
   * @see io.cosmosoftware.kite.dao.SampleData#makeSampleData()
   */
  @Override
  public SampleData makeSampleData() {
    this.name = "Delay 10 ms";
    this.delay = 10;
    this.bandwidth = 0;
    this.corrupt = 0;
    this.duplicate = 0;
    this.packetloss = 0;

    return this;
  }
}
