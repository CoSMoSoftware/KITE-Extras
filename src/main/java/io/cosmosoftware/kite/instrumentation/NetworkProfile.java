/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.instrumentation;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.report.Status;

import javax.json.JsonObject;

public class NetworkProfile {

  private final Profile profile;

  private final String name;


  /**
   * Instantiates a new Instance.
   *
   * @param jsonObject the json object
   */
  public NetworkProfile(JsonObject jsonObject) throws KiteTestException {
    String missingKey = "";
    try {
      missingKey = "name";
      this.name = jsonObject.getString("name");
      missingKey = "profile";
      this.profile = new Profile(jsonObject.getJsonObject("profile"));

    } catch (Exception e) {
      throw new KiteTestException("Error in json config instrumentation, the key " + missingKey + " is missing.", Status.BROKEN, e);
    }

  }

  /**
   * Gets profile.
   *
   * @return the profile
   */
  public Profile getProfile() {
    return profile;
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

}
