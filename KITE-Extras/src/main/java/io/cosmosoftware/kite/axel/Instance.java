/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.cosmosoftware.kite.axel;

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

  private String id;

  private String username;

  private String name;

  private String ipAddress;

  private String keyFilePath;

  private String type;

  private String password;

  private String nit0;

  private String nit1;

  private String nit2;

  /**
   * Instantiates a new Instance.
   */
  public Instance() {
    super();
  }

  /**
   * Instantiates a new Instance.
   *
   * @param jsonObject the json object
   */
  public Instance(JsonObject jsonObject) {
    this.id = jsonObject.getString("_id");
    this.ipAddress = jsonObject.getString("ipAddress");
    this.username = jsonObject.getString("username");
    this.name = jsonObject.getString("name");
    this.keyFilePath = jsonObject.getString("keyFilePath");
    this.type = jsonObject.getString("type");
    this.password = jsonObject.getString("password");
    this.nit0 = jsonObject.getString("nit0");
    this.nit1 = jsonObject.getString("nit1");
    this.nit2 = jsonObject.getString("nit2");
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
   * Sets id.
   *
   * @param id the id
   */
  public void setId(String id) {
    this.id = id;
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
   * Sets username.
   *
   * @param username the username
   */
  public void setUsername(String username) {
    this.username = username;
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
   * Sets ip address.
   *
   * @param ipAddress the ip address
   */
  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
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
   * Sets key file path.
   *
   * @param keyFilePath the key file path
   */
  public void setKeyFilePath(String keyFilePath) {
    this.keyFilePath = keyFilePath;
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
   * Sets type.
   *
   * @param type the type
   */
  public void setType(String type) {
    this.type = type;
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
   * Sets password.
   *
   * @param password the password
   */
  public void setPassword(String password) {
    this.password = password;
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
   * Sets nit0.
   *
   * @param nit0 the nit0
   */
  public void setNit0(String nit0) {
    this.nit0 = nit0;
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
   * Sets nit1.
   *
   * @param nit1 the nit1
   */
  public void setNit1(String nit1) {
    this.nit1 = nit1;
  }

  /**
   * Gets nit2.
   *
   * @return the nit2
   */
  public String getNit2() {
    return nit2;
  }

  /**
   * Sets nit2.
   *
   * @param nit2 the nit2
   */
  public void setNit2(String nit2) {
    this.nit2 = nit2;
  }

}
