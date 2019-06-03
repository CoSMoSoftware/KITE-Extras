/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.instrumentation;

import io.cosmosoftware.kite.exception.KiteTestException;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.HashMap;

/**
 * The type Instrumentation.
 */
public class Instrumentation extends HashMap<String, Instance> {

  private final String remoteAddress;
  private final String instrumentUrl;
  private String kiteServerGridId = null;

  /**
   * Instantiates a new Instrumentation.
   *
   * @param jsonObject the json object
   */
  public Instrumentation(JsonObject jsonObject, String instrumentUrl, String remoteAddress) throws KiteTestException {
    this.instrumentUrl = instrumentUrl;
    this.remoteAddress = remoteAddress;
    JsonArray jsonArray = jsonObject.getJsonArray("instances");
    for (int i = 0; i < jsonArray.size(); i++) {
      try {
        Instance instance = new Instance(jsonArray.getJsonObject(i));
        this.put(instance.getId(), instance);
      } catch (KiteTestException e) {
        throw e;
      }
    }
  }

  public String getRemoteAddress() {
    return this.remoteAddress;
  }

  public String getInstrumentUrl() {
    return this.instrumentUrl;
  }

  public void setKiteServerGridId(String kiteServerGridId) {
    this.kiteServerGridId = kiteServerGridId;
  }

  public String getKiteServerGridId() {
    return this.instrumentUrl;
  }
}
