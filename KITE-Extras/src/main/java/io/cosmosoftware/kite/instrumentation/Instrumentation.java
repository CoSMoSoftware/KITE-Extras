/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.instrumentation;

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
  public Instrumentation(JsonObject jsonObject) {
    JsonArray jsonArray = jsonObject.getJsonArray("instances");
    for (int i = 0; i < jsonArray.size(); i++) {
      Instance instance = new Instance(jsonArray.getJsonObject(i));
      this.put(instance.getId(), instance);
    }
  }
  
}
