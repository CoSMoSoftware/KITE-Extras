/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.instrumentation;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.report.Status;
import io.cosmosoftware.kite.util.TestUtils;
import java.util.HashMap;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 * The type Instrumentation.
 */
public class NetworkInstrumentation {

  private final String remoteAddress;
  private final HashMap<String, Instance> instances;
  private final String kiteServerGridId;
  private final HashMap<String, NetworkProfile> networkProfiles;
  private final String kiteServer;
  private final JsonObject jsonObject;

  /**
   * Constructor for the KITE Engine Test Manager.
   *
   * @param jsonObject the json object
   */
  public NetworkInstrumentation(JsonObject jsonObject, String remoteAddress)
      throws KiteTestException {
    this.remoteAddress = remoteAddress;
    this.kiteServerGridId = null;
    this.kiteServer = null;
    this.jsonObject = jsonObject;
    JsonArray jsonArray = TestUtils.getJsonArray(jsonObject, "instances");
    this.instances = new HashMap<>();
    for (int i = 0; i < jsonArray.size(); i++) {
      try {
        Instance instance = new Instance(jsonArray.getJsonObject(i));
        this.instances.put(instance.getId(), instance);
      } catch (Exception e) {
        throw new KiteTestException("Error in json config instances.", Status.BROKEN, e);
      }
    }
    jsonArray = TestUtils.getJsonArray(jsonObject, "networkProfiles");
    this.networkProfiles = new HashMap<>();
    for (int i = 0; i < jsonArray.size(); i++) {
      String missingKey = "";
      try {
        missingKey = "networkProfile";
        NetworkProfile networkProfile = new NetworkProfile(
            jsonArray.getJsonObject(i));
        this.networkProfiles.put(networkProfile.getName(), networkProfile);
      } catch (Exception e) {
        throw new KiteTestException(
            "Error in json config networkProfiles, the key " + missingKey + " is missing.",
            Status.BROKEN, e);
      }
    }
  }

  /**
   * Constructor for the KITE Server Test Manager
   */
  public NetworkInstrumentation(List<NetworkProfile> networkProfiles, String kiteServer, String remoteAddress, String kiteServerGridId) throws KiteTestException {
    this.instances = null;
    this.jsonObject = null;
    this.kiteServerGridId = kiteServerGridId;
    this.remoteAddress = remoteAddress;
    this.kiteServer = kiteServer;
    this.networkProfiles = new HashMap<>();
    for (int i = 0; i < networkProfiles.size(); i++) {
      String missingKey = "networkProfiles";
      try {
        if (networkProfiles.get(i).getCommand() == null) {
          networkProfiles.get(i).setCommand();
        }
        networkProfiles.get(i).setNit("eth9");
        if (networkProfiles.get(i).getCleanUpCommand() == null) {
          networkProfiles.get(i).setCleanUpCommand();
        }
        this.networkProfiles.put(networkProfiles.get(i).getName(), networkProfiles.get(i));
      } catch (Exception e) {
        throw new KiteTestException(
            "Error in json config networkProfiles, the key " + missingKey + " is missing.",
            Status.BROKEN, e);
      }
    }
  }


  public String getRemoteAddress() {
    return this.remoteAddress;
  }

  public HashMap<String, Instance> getInstances() {
    return this.instances;
  }

  public HashMap<String, NetworkProfile> getNetworkProfiles() {
    return this.networkProfiles;
  }

  public String getKiteServerGridId() {
    return this.kiteServerGridId;
  }

  public String getKiteServer() {
    return this.kiteServer;
  }

  public JsonObject getJsonObject() {
    return this.jsonObject;
  }

}
