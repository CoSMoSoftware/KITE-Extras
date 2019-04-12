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

import io.cosmosoftware.kite.manager.SSHManager;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.cosmosoftware.kite.util.TestUtils.downloadFile;

/**
 * The type Instrumentation.
 */
public class Instrumentation {

  private List<Instance> instances = new ArrayList<Instance>();

  private List<Scenario> scenarios = new ArrayList<Scenario>();

  /**
   * Instantiates a new Instrumentation.
   */
  public Instrumentation() {
    super();
  }

  /**
   * Instantiates a new Instrumentation.
   *
   * @param jsonObject the json object
   */
  public Instrumentation(JsonObject jsonObject) {
    this();

    // Mandatory
    JsonArray jsonArray = jsonObject.getJsonArray("instances");
    for (int i = 0; i < jsonArray.size(); i++) {
      this.instances.add(new Instance(jsonArray.getJsonObject(i)));
    }

    jsonArray = jsonObject.getJsonArray("scenarios");
    for (int i = 0; i < jsonArray.size(); i++) {
      this.scenarios.add(new Scenario(jsonArray.getJsonObject(i)));
    }
    this.assignInstances();
  }

  /**
   * Gets instances.
   *
   * @return the instances
   */
  public List<Instance> getInstances() {
    return instances;
  }

  /**
   * Sets instances.
   *
   * @param instances the instances
   */
  public void setInstances(List<Instance> instances) {
    this.instances = instances;
  }

  /**
   * Gets scenarios.
   *
   * @return the scenarios
   */
  public List<Scenario> getScenarios() {
    return scenarios;
  }

  /**
   * Sets scenarios.
   *
   * @param scenarios the scenarios
   */
  public void setScenarios(List<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

  private void assignInstances() {
    Map<String, Instance> instances = new HashMap<String, Instance>();
    for (Instance instance : this.instances) {
      instances.put(instance.getId(), instance);
    }

    for (Scenario scenario : this.scenarios) {
      scenario.setInstance(instances.get(scenario.getInstanceId()));
    }
  }

  /**
   * Run command.
   *
   * @param index the index
   * @return the name of the command run
   * @throws Exception the exception
   */
  public String runCommand(int index) throws Exception {
    Scenario scenario = this.scenarios.get(index);
    Instance gateway = scenario.getInstance();

    // Download pem file
    String keyFileUrl = gateway.getKeyFilePath();
    String keyFilePath =
            System.getProperty("java.io.tmpdir") + new File(new URI(keyFileUrl).getPath()).getName();
    downloadFile(keyFileUrl, keyFilePath);

    // Develop and run the commands
    new SSHManager(keyFilePath, gateway.getUsername(), gateway.getIpAddress(),
            scenario.makeCommand()).call();
    return scenario.getName() ;
  }

  /**
   * Run clean command.
   *
   * @param index the index
   * @throws Exception the exception
   */
  public void runCleanCommand(int index) throws Exception {
    Scenario scenario = this.scenarios.get(index);
    Instance gateway = scenario.getInstance();

    // Download pem file
    String keyFileUrl = gateway.getKeyFilePath();
    String keyFilePath =
            System.getProperty("java.io.tmpdir") + new File(new URI(keyFileUrl).getPath()).getName();
    downloadFile(keyFileUrl, keyFilePath);

    // Develop and run the commands
    new SSHManager(keyFilePath, gateway.getUsername(), gateway.getIpAddress(),
            scenario.makeCleanCommand()).call();
  }
  
}
