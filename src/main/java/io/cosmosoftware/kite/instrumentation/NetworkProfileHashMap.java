/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.instrumentation;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.report.Status;

import javax.json.JsonArray;
import java.util.HashMap;

/**
 * The type Networks.
 */
public class NetworkProfileHashMap extends HashMap<String, Profile> {

  
  /**
   * Instantiates a new Network List.
   *
   * @param jsonArray the json array
   */
  public NetworkProfileHashMap(JsonArray jsonArray) throws KiteTestException {
    for (int i = 0; i < jsonArray.size(); i++) {
      String missingKey = "";
      try {
        missingKey = "name";
        String name = jsonArray.getJsonObject(i).getString(missingKey);
        missingKey = "profile";
        Profile profile = new Profile(jsonArray.getJsonObject(i).getJsonObject(missingKey));
        this.put(name, profile);

      } catch (Exception e) {
        throw new KiteTestException("Error in json config instrumentation, the key " + missingKey + " is missing.", Status.BROKEN, e);
      }
    }
  }


  /**
   * Instantiates a new Network List.
   *
   * @param networkProfilesPath the path to the networkProfiles config
   */
  public NetworkProfileHashMap(String networkProfilesPath) throws KiteTestException {
    System.setProperty("networkProfiles", networkProfilesPath);
    
    //todo: read array from the file.
    JsonArray jsonArray = null;   
    
    for (int i = 0; i < jsonArray.size(); i++) {
      NetworkProfile network = new NetworkProfile(jsonArray.getJsonObject(i));
      this.put(network.getName(), network);
    }
  }

}
