/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.instrumentation;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.JsonBuilder;
import io.cosmosoftware.kite.report.Status;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import static io.cosmosoftware.kite.util.ReportUtils.getStackTrace;

/**
 * The type Instance: represents a selenium node or a gateway for network instrumentation. { "_id":
 * "gw1", "type": "gateway", "name": "Alice Gateway", "ipAddress": "11.222.33.238", "username":
 * "gw-user", "password": "optional", "keyFilePath": "file://path/to/privatekey.pem", "nit0":
 * "eth0", "nit1": "eth1", "nit2": "eth2", },
 */
public class Instance implements JsonBuilder {

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
      throw new KiteTestException("The key " + missingKey + " is missing", Status.FAILED, e);
    }
    this.name = jsonObject.getString("name", this.id);
    this.password = jsonObject.getString("password", null);
  }

  @Override
  public String toString() {
    try {
      return buildJsonObjectBuilder().build().toString();
    } catch (NullPointerException e) {
      return getStackTrace(e);
    }
  }

  @Override
  public JsonObjectBuilder buildJsonObjectBuilder() throws NullPointerException {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    if (this.id != null) {
      builder.add("_id", this.id);
    }
    if (this.username != null) {
      builder.add("username", this.username);
    }
    if (this.name != null) {
      builder.add("name", this.name);
    }
    if (this.ipAddress != null) {
      builder.add("ipAddress", this.ipAddress);
    }
    if (this.keyFilePath != null) {
      builder.add("keyFilePath", this.keyFilePath);
    }
    if (this.type != null) {
      builder.add("type", this.type);
    }
    if (this.password != null) {
      builder.add("password", this.password);
    }
    if (this.nit0 != null) {
      builder.add("nit0", this.nit0);
    }
    if (this.nit1 != null) {
      builder.add("nit1", this.nit1);
    }
    if (this.nit2 != null) {
      builder.add("nit2", this.nit2);
    }
    return builder;
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
