/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.instrumentation;

import io.cosmosoftware.kite.exception.KiteTestException;

import javax.json.JsonArray;
import java.util.HashMap;

/**
 * The type Networks.
 */
public class Networks extends HashMap<String, Network> {


  /**
   * Instantiates a new Network List.
   *
   * @param jsonArray the json array
   */
  public Networks(JsonArray jsonArray) throws KiteTestException {
    for (int i = 0; i < jsonArray.size(); i++) {
      try {
        Network network = new Network(jsonArray.getJsonObject(i));
        this.put(network.getName(), network);
      } catch (KiteTestException e) {
        throw e;
      }
    }
  }

}
