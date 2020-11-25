/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.steps;

import static io.cosmosoftware.kite.util.ReportUtils.timestamp;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.util.ReportUtils;
import javax.json.Json;
import javax.json.JsonObject;

public class ConsoleLogStep extends TestStep {

  public ConsoleLogStep(Runner runner) {
    super(runner);
    setStepPhase(StepPhase.ALL);
    setOptional(true);
    setIgnoreBroken(true);
    setScreenShotOnFailure(false);
    setSilent(true);
  }

  @Override
  protected void step()  {
    try {
      String log = ReportUtils.consoleLogs(this.webDriver);
      JsonObject jsonLog = Json.createObjectBuilder()
          .add("Timestamp", timestamp())
          .add("Console Log", log).build();
      this.reporter.jsonAttachment(this.report, "Console Logs", jsonLog);
    } catch (Exception e) {
      // ignore
      String message = e.getMessage();
    }
  }

  @Override
  public String stepDescription() {
    return "Get the browser console logs.";
  }


}
