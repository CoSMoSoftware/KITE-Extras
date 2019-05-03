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


  /**
   * Instantiates a new Instrumentation.
   *
   * @param jsonObject the json object
   */
  public Instrumentation(JsonObject jsonObject) throws KiteTestException {
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
  
}
