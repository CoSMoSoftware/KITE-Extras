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
import java.util.List;


@Entity(name = NetworkProfile.TABLE_NAME)
public class NetworkProfile extends KiteEntity implements SampleData {

  final static String TABLE_NAME = "networkprofiles";
  private String id = "";
  private String command = "";
  private String cleanUpCommand = "";
  private Integer delay = 0;
  private Integer packetloss = 0;
  private Integer corrupt = 0;
  private Integer duplicate = 0;
  private Integer bandwidth = 0;
  private String name = "";

  public NetworkProfile() {
    super();
  }

  public NetworkProfile(JsonObject jsonObject) throws Exception {
    this.name = jsonObject.getString("name");
    this.delay =  jsonObject.getInt("delay", 0);
    this.packetloss = jsonObject.getInt("packetloss", 0);
    this.corrupt = jsonObject.getInt("corrupt", 0);
    this.duplicate = jsonObject.getInt("duplicate", 0);
    this.bandwidth = jsonObject.getInt("bandwidth", 0);
    this.command = jsonObject.getString("command", null);
    this.cleanUpCommand = jsonObject.getString("cleanUpCommand", null);
  }

  protected String defaultCommand(String nit) throws KiteTestException {
    String egress_command = ""; // command for traffic going out
    String ingress_command = ""; // command for traffic going in
    String commandStr =
        "sudo ip link add ifb0 type ifb || true && sudo ip link set up dev ifb0 || true && sudo tc qdisc add dev "
            + nit + " ingress || true && sudo tc filter add dev " + nit
            + " parent ffff: protocol ip u32 match u32 0 0 action mirred egress redirect dev ifb0 || true && ";
    if (this.delay != 0) {
      egress_command = createCommand(egress_command, "delay " + delay + "ms ", nit);
      ingress_command = createCommand(ingress_command, "delay " + delay + "ms ", "ifb0");
    }
    if (this.packetloss != 0) {
      egress_command = createCommand(egress_command, "loss " + packetloss + "% ", nit);
      ingress_command = createCommand(ingress_command, "loss " + packetloss + "% ", "ifb0");
    }
    if (this.corrupt != 0) {
      egress_command = createCommand(egress_command, "corrupt " + corrupt + "% ", nit);
      ingress_command = createCommand(ingress_command, "corrupt " + corrupt + "% ", "ifb0");
    }
    if (this.duplicate != 0) {
      egress_command = createCommand(egress_command, "duplicate " + duplicate + "% ", nit);
      ingress_command = createCommand(ingress_command, "duplicate " + duplicate + "% ", "ifb0");
    }
    if (this.bandwidth != 0) {
      egress_command = createCommand(egress_command, "rate " + bandwidth + "kbit ", nit);
      ingress_command = createCommand(ingress_command, "rate " + bandwidth + "kbit ", "ifb0");
    }
    commandStr += egress_command + "|| true && " + ingress_command;
    if (egress_command.equals("") && ingress_command.equals("")) {
      throw new KiteTestException("No command to run.", Status.BROKEN);
    }
    return commandStr;
  }

  protected String defaultCleanUpCommand(String nit) {
    String commandStr = "sudo tc qdisc del dev " + nit + " root || true ";
    commandStr += "&& sudo tc qdisc del dev " + nit + " ingress || true && ";
    commandStr += "sudo tc qdisc del dev ifb0 root || true";
    return commandStr;
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

  public Integer getBandwidth() {
    return bandwidth;
  }

  public void setBandwidth(Integer bandwidth) {
    this.bandwidth = bandwidth;
  }

  public Integer getCorrupt() {
    return corrupt;
  }

  public void setCorrupt(Integer corrupt) {
    this.corrupt = corrupt;
  }

  public Integer getDelay() {
    return delay;
  }

  public void setDelay(Integer delay) {
    this.delay = delay;
  }

  public Integer getDuplicate() {
    return duplicate;
  }

  public void setDuplicate(Integer duplicate) {
    this.duplicate = duplicate;
  }

  public Integer getPacketloss() {
    return packetloss;
  }

  public void setPacketloss(Integer packetloss) {
    this.packetloss = packetloss;
  }

  public String getCommand() {
    return this.command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public String getCleanUpCommand() {
    return this.cleanUpCommand;
  }

  public void setCleanUpCommand(String cleanUpCommand) {
    this.cleanUpCommand = cleanUpCommand;
  }
  
  @Transient
  public String getCommand(List<String> nit) throws KiteTestException {
    String commandStr = this.command != null ? this.command : defaultCommand(nit.get(0));
    for (int i = 0; i < nit.size(); i++) {
      commandStr = commandStr.replaceAll("%nit" + i, nit.get(i));
    }
    return commandStr;
  }

  @Transient
  public String getCleanUpCommand(List<String> nit) throws KiteTestException {
    String commandStr = this.cleanUpCommand != null ? this.cleanUpCommand : defaultCleanUpCommand(nit.get(0));
    for (int i = 0; i < nit.size(); i++) {
      commandStr = commandStr.replaceAll("%nit" + i, nit.get(i));
    }
    return commandStr;
  }
  
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
