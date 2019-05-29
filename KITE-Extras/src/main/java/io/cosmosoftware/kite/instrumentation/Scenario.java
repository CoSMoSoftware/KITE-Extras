/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.instrumentation;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.manager.SSHManager;
import io.cosmosoftware.kite.report.Status;
import io.cosmosoftware.kite.util.ReportUtils;
import net.bytebuddy.implementation.bytecode.Throw;
import org.apache.log4j.Logger;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;

public class Scenario {

  private final String type;
  private final String name;
  private final String command;
  private final String network;
  private final String gateway;
  private ArrayList<Integer> clientIds = new ArrayList<>();
  private final Networks networks;
  private final Logger logger;

  public Scenario(JsonObject jsonObject, Logger logger, Networks networks) throws Exception {
    this.logger = logger;
    String missingKey="";
    this.networks = networks;
    try {
      missingKey = "type";
      this.type = jsonObject.getString("type");
      if (this.type.equals("client")) {
        missingKey = "clientIds";
        JsonArray jsonArray = jsonObject.getJsonArray("clientIds");
        for (int i = 0; i < jsonArray.size(); i++) {
          this.clientIds.add(jsonArray.getInt(i));
        }
        this.gateway = null;
      } else if (this.type.equals("gateway")) {
        missingKey = "gateway";
        this.gateway = jsonObject.getString("gateway");
        this.clientIds = null;
      } else {
        throw new KiteTestException("The type specified doesn't exist", Status.FAILED);
      }
      missingKey = "network";
      this.network = jsonObject.getString("network");
      NWCommands nwCommands = new NWCommands(this.networks.get(this.network).getProfile());
      this.command = nwCommands.getCommand();
      missingKey = "name";
      name = jsonObject.getString("name");
    } catch (Exception e) {
      throw new KiteTestException("The key " + missingKey + " is missing", Status.FAILED, e);
    }
  }

  public String getName() {
    return name;
  }

  public ArrayList<Integer> getClientIds() {
    return clientIds;
  }

  public String getCommand() {
    return command;
  }

  public String getGateway() {
    return gateway;
  }

  public String getType() {
    return type;
  }
}
