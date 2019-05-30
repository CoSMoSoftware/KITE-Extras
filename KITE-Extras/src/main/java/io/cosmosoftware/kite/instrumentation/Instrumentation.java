/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.instrumentation;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.manager.SSHManager;
import io.cosmosoftware.kite.util.ReportUtils;
import io.cosmosoftware.kite.util.TestUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.awt.*;
import java.net.URI;
import java.util.HashMap;

import static io.cosmosoftware.kite.util.TestUtils.getPrivateIp;

/**
 * The type Instrumentation.
 */
public class Instrumentation extends HashMap<String, Instance> {


  /**
   * Instantiates a new Instrumentation.
   *
   * @param jsonObject the json object
   */
  public Instrumentation(JsonObject jsonObject) throws KiteTestException {
    JsonArray jsonArray = jsonObject.getJsonArray("instances");
    for (int i = 0; i < jsonArray.size(); i++) {
      try {
        Instance instance = new Instance(jsonArray.getJsonObject(i));
        this.put(instance.getId(), instance);
      } catch (KiteTestException e) {
        throw e;
      }
    }
  }
}
