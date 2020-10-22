/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.steps;

import static io.cosmosoftware.kite.util.TestUtils.curl;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.util.ReportUtils;
import java.net.URL;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class GetSystemInfoStep extends TestStep {
  private final String PORT = "2807";
  private String ADDRESS = "NC";
  public GetSystemInfoStep(Runner runner) {
    super(runner);
    setOptional(true);
    setIgnoreBroken(true);
    setScreenShotOnFailure(false);
    setSilent(true);
    if (runner.getPublicIpAddress() != null && !runner.getPublicIpAddress().equals("NC")) {
      this.ADDRESS = runner.getPublicIpAddress();
    }
  }

  @Override
  protected void step() throws KiteTestException {
    JsonObjectBuilder res = Json.createObjectBuilder();
    String url = this.ADDRESS + ":" + this.PORT + "/";
    if (!this.ADDRESS.equals("NC")) {
      res.add("system", curl(url + "system"));
      res.add("cpu", curl(url + "system/cpu"));
      res.add("mem", curl(url + "system/mem"));
    }
    JsonObject resObject = res.build();
    logger.debug("System usage: " + resObject);
    reporter.jsonAttachment(this.report, "sys-info", resObject);
  }

  @Override
  public String stepDescription() {
    return "Get system usage information.";
  }


}
