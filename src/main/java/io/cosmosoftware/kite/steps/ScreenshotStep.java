package io.cosmosoftware.kite.steps;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.report.Reporter;

import static io.cosmosoftware.kite.util.ReportUtils.saveScreenshotPNG;
import static io.cosmosoftware.kite.util.ReportUtils.timestamp;

public class ScreenshotStep extends TestStep {

  public ScreenshotStep(Runner runner) {
    super(runner);
    setStepPhase(StepPhase.ALL);
    setOptional(true);
  }

  @Override
  protected void step() throws KiteTestException {
    Reporter.getInstance()
      .screenshotAttachment(
        report, "ScreenshotStep_" + timestamp(), saveScreenshotPNG(webDriver));
  }

  @Override
  public String stepDescription() {
    return "Get a screenshot";
  }
}
