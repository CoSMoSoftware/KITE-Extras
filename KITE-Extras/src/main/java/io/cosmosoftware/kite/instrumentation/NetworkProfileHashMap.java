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
public class NetworkProfileHashMap extends HashMap<String, NetworkProfile> {


  /**
   * Instantiates a new Network List.
   *
   * @param jsonArray the json array
   */
  public NetworkProfileHashMap(JsonArray jsonArray) throws KiteTestException {
    for (int i = 0; i < jsonArray.size(); i++) {
      try {
        NetworkProfile network = new NetworkProfile(jsonArray.getJsonObject(i));
        this.put(network.getName(), network);
      } catch (KiteTestException e) {
        throw e;
      }
    }
  }

}
