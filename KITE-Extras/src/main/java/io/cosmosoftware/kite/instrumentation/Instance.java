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

package io.cosmosoftware.kite.instrumentation;

import javax.json.JsonObject;

/**
 * The type Instance: represents a selenium node or a gateway for network instrumentation.
 */
public class Instance {

  private String id;

  private String username;

  private String name;

  private String ipAddress;

  private String keyFilePath;

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

}
