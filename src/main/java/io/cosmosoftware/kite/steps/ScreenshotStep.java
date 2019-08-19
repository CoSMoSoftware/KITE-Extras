/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.steps;

import static io.cosmosoftware.kite.util.ReportUtils.saveScreenshotPNG;
import static io.cosmosoftware.kite.util.ReportUtils.timestamp;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.report.Reporter;

public class ScreenshotStep extends TestStep {

  public ScreenshotStep(Runner runner) {
    super(runner);
    setStepPhase(StepPhase.ALL);
    setOptional(true);
  }

  @Override
  protected void step() throws KiteTestException {
    reporter
        .screenshotAttachment(
            report, "ScreenshotStep_" + timestamp(), saveScreenshotPNG(webDriver));
  }

  @Override
  public String stepDescription() {
    return "Get a screenshot";
  }
}
