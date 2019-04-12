/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.cosmosoftware.kite.instrumentation;

import org.apache.log4j.Logger;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Scenario.
 */
public class Scenario {

  private String instanceId;

  private static final Logger logger = Logger.getLogger(Scenario.class.getName());
  private Instance instance;

  private List<String> interfaces = new ArrayList<String>();

  private long bandwidth = -1;

  private int latency = -1;

  private int packetloss = -1;
  private int corruption = -1;
  private int reordering = -1;
  private int duplication = -1;
  private int correlation = -1;
  private int jitter = -1;

  private String name = "";

  private List<String> commands = new ArrayList<String>();

  private final String pipe = " || true && ";
  /**
   * Instantiates a new Scenario.
   */
  public Scenario() {
    super();
  }

  /**
   * Instantiates a new Scenario.
   *
   * @param jsonObject the json object
   */
  public Scenario(JsonObject jsonObject) {
    this();

    // Mandatory
    this.instanceId = jsonObject.getString("instanceId");
    JsonArray jsonArray = jsonObject.getJsonArray("interfaces");
    for (int i = 0; i < jsonArray.size(); i++) {
      this.interfaces.add(jsonArray.getString(i));
    }
    // Optional
    this.name = jsonObject.getString("name", this.name);
    this.latency = jsonObject.getInt("latency", this.latency);
    this.jitter = jsonObject.getInt("jitter", this.jitter);
    this.packetloss = jsonObject.getInt("packetLoss", this.packetloss);
    this.corruption = jsonObject.getInt("corruption", this.corruption);
    this.reordering = jsonObject.getInt("reordering", this.reordering);
    this.duplication = jsonObject.getInt("duplication", this.duplication);
    this.correlation = jsonObject.getInt("correlation", this.correlation);

    JsonNumber jsonNumber = jsonObject.getJsonNumber("bandwidth");
    if (jsonNumber != null) {
      this.bandwidth = jsonNumber.longValueExact();
    }

    jsonArray = jsonObject.getJsonArray("commands");
    if (jsonArray != null) {
      for (int i = 0; i < jsonArray.size(); i++) {
        this.commands.add(jsonArray.getString(i));
      }
    }
  }

  /**
   * Gets name of the scenario
   *
   * @return the scenario name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets instance id.
   *
   * @return the instance id
   */
  public String getInstanceId() {
    return instanceId;
  }

  /**
   * Sets instance id.
   *
   * @param instanceId the instance id
   */
  public void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }

  /**
   * Gets instance.
   *
   * @return the instance
   */
  public Instance getInstance() {
    return instance;
  }

  /**
   * Sets instance.
   *
   * @param instance the instance
   */
  public void setInstance(Instance instance) {
    this.instance = instance;
    if (this.instance.getName() != null) {
      this.name = this.name + " on " + this.instance.getName();
    }
  }

  /**
   * Gets interfaces.
   *
   * @return the interfaces
   */
  public List<String> getInterfaces() {
    return interfaces;
  }

  /**
   * Sets interfaces.
   *
   * @param interfaces the interfaces
   */
  public void setInterfaces(List<String> interfaces) {
    this.interfaces = interfaces;
  }

  /**
   * Gets bandwidth.
   *
   * @return the bandwidth
   */
  public long getBandwidth() {
    return bandwidth;
  }

  /**
   * Sets bandwidth.
   *
   * @param bandwidth the bandwidth
   */
  public void setBandwidth(long bandwidth) {
    this.bandwidth = bandwidth;
  }

  /**
   * Gets latency.
   *
   * @return the latency
   */
  public int getLatency() {
    return latency;
  }

  /**
   * Sets latency.
   *
   * @param latency the latency
   */
  public void setLatency(int latency) {
    this.latency = latency;
  }

  /**
   * Gets packetloss.
   *
   * @return the packetloss
   */
  public int getPacketloss() {
    return packetloss;
  }

  /**
   * Sets packetloss.
   *
   * @param packetloss the packetloss
   */
  public void setPacketloss(int packetloss) {
    this.packetloss = packetloss;
  }

  /**
   * Gets commands.
   *
   * @return the commands
   */
  public List<String> getCommands() {
    return commands;
  }

  /**
   * Sets commands.
   *
   * @param commands the commands
   */
  public void setCommands(List<String> commands) {
    this.commands = commands;
  }


  /**
   * @param nic the network interface name (e.g. eth1)
   * @return the tc command to clean this as a String
   */
  private String cleanCommand(String nic) {
    return String.format("sudo tc qdisc del dev %s root" + pipe, nic);
  }

  /**
   * @param nic the network interface name (e.g. eth1)
   * @return the tc command to show what was applied on this interface
   */
  private String showCommand(String nic) {
    return String.format("sudo tc qdisc show dev %s" + pipe, nic);
  }

  private String tcCommand(String nic, String netemCmd, int value, int latency, int correlation) {
    String command = String.format("sudo tc qdisc add dev %s root netem %s ",
            nic, netemCmd);
    if (value > 0) {
      command += String.format("%d%% ", value);
      if (correlation > 0) {
        command += String.format("%d%% ", correlation);
      }
    }
    if (latency > 0) {
      command += "delay "+ latency + "ms ";
    }
    return command + pipe;
  }


  /**
   * Make command string.
   *
   * @return the command string
   */
  public String makeCommand() {
    StringBuilder builder = new StringBuilder();
    for (String nic : this.interfaces) {
      boolean noCommand = true;
      //first, clean up the interface.
      builder.append(cleanCommand(nic));

      if (this.packetloss > 0) {
        builder.append(tcCommand(nic, "loss", this.packetloss, this.correlation, this.latency));
        noCommand = false;
      }
      if (this.corruption > 0) {
        builder.append(tcCommand(nic, "corrupt", this.corruption, this.correlation, this.latency));
        noCommand = false;
      }
      if (this.duplication > 0) {
        builder.append(tcCommand(nic, "duplicate", this.duplication, this.correlation, this.latency));
        noCommand = false;
      }

      if (this.jitter > 0) {
        if (this.latency > 0) {
          builder.append(String.format(
                  "sudo tc qdisc add dev %s root netem delay %dms %dms distribution normal" + pipe,
                  nic, this.latency, this.jitter));
        } else {
          logger.error("Both latency and jitter must be > 0 for jitter to be applied. Provided: jitter = "
                  + this.jitter + " latency = " + this.latency);
        }
      }

      //bandwith and latency command are mutually exclusive, since
      //bandwidth command already includes latency
      if (this.bandwidth > 0) {
        int latency = this.latency > 0 ? this.latency : 10;
        builder.append(String.format("sudo tc qdisc add dev %s root tbf rate %dkbit burst 10kb latency %dms" + pipe,
                nic, this.bandwidth, latency));
        noCommand = false;
      }
      if (noCommand && this.latency > 0) {
        builder.append(tcCommand(nic, "", 0, this.latency, 0));
      }
      for (String command : this.commands) {
        builder.append(command + pipe);
      }
    }
    for (String nic : this.interfaces) {
      builder.append(showCommand(nic));
    }
    return buildCommand(builder);
  }

  /**
   * Make clean command string.
   *
   * @return the string
   */
  public String makeCleanCommand() {
    StringBuilder builder = new StringBuilder();
    for (String nic : this.interfaces) {
      builder.append(cleanCommand(nic));
    }
    return buildCommand(builder);
  }

  private String buildCommand(StringBuilder builder) {
    String s = builder.toString();
    s = s.contains(pipe) ? s.substring(0, s.lastIndexOf(pipe)) : s;
    return s;
  }

}
