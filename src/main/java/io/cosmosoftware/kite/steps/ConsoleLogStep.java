package io.cosmosoftware.kite.steps;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.report.Reporter;
import io.cosmosoftware.kite.util.ReportUtils;

public class ConsoleLogStep extends TestStep {

  public ConsoleLogStep(Runner runner) {
    super(runner);
    setStepPhase(StepPhase.ALL);
  }

  @Override
  protected void step() throws KiteTestException {
    Reporter.getInstance().textAttachment(report, "Console Logs", 
      ReportUtils.consoleLogs(webDriver), "plain");
  }

  @Override
  public String stepDescription() {
    return "Get the browser console logs.";
  }

  
}
