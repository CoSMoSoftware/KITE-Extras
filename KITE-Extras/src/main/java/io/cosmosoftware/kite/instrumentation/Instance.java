/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.instrumentation;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.report.Status;

import javax.json.JsonObject;

/**
 * The type Instance: represents a selenium node or a gateway for network instrumentation.
 *     {
 *       "_id": "gw1",
 * 	  "type": "gateway",
 *       "name": "Alice Gateway",
 *       "ipAddress": "11.222.33.238",
 *       "username": "gw-user",
 *       "password": "optional",
 *       "keyFilePath": "file://path/to/privatekey.pem",
 * 	  "nit0": "eth0",
 * 	  "nit1": "eth1",
 * 	  "nit2": "eth2",
 *     },
 */
public class Instance {

  private final String id;

  private final String username;

  private final String name;

  private final String ipAddress;

  private final String keyFilePath;

  private final String type;

  private final String password;

  private final String nit0;

  private final String nit1;

  private final String nit2;


  /**
   * Instantiates a new Instance.
   *
   * @param jsonObject the json object
   */
  public Instance(JsonObject jsonObject) throws KiteTestException {
    String missingKey = "";
    try {
      missingKey = "_id";
      this.id = jsonObject.getString("_id");
      missingKey = "ipAddress";
      this.ipAddress = jsonObject.getString("ipAddress");
      missingKey = "username";
      this.username = jsonObject.getString("username");
      missingKey = "keyFilePath";
      this.keyFilePath = jsonObject.getString("keyFilePath");
      missingKey = "type";
      this.type = jsonObject.getString("type");
      missingKey = "nit0";
      this.nit0 = jsonObject.getString("nit0");
      missingKey = "nit1";
      this.nit1 = jsonObject.getString("nit1");
      missingKey = "nit2";
      this.nit2 = jsonObject.getString("nit2");
    } catch (NullPointerException e) {
      throw new KiteTestException("Error in json config instrumentation, the key " + missingKey + " is missing.", Status.BROKEN, e);
    }
    this.name = jsonObject.getString("name", this.id);
    this.password = jsonObject.getString("password", null);
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets username.
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Gets ip address.
   *
   * @return the ip address
   */
  public String getIpAddress() {
    return ipAddress;
  }

  /**
   * Gets key file path.
   *
   * @return the key file path
   */
  public String getKeyFilePath() {
    return keyFilePath;
  }

  /**
   * Gets type.
   *
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * Gets password.
   *
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Gets nit0.
   *
   * @return the nit0
   */
  public String getNit0() {
    return nit0;
  }

  /**
   * Gets nit1.
   *
   * @return the nit1
   */
  public String getNit1() {
    return nit1;
  }

  /**
   * Gets nit2.
   *
   * @return the nit2
   */
  public String getNit2() {
    return nit2;
  }

}
