package io.cosmosoftware.kite.steps;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.report.Reporter;
import io.cosmosoftware.kite.util.ReportUtils;
import org.openqa.selenium.WebDriver;

public class ConsoleLogStep extends TestStep {

  public ConsoleLogStep(StepParams params) {
    super(params);
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
