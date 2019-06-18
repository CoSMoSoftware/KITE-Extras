package io.cosmosoftware.kite.steps;

import io.cosmosoftware.kite.report.KiteLogger;
import org.openqa.selenium.WebDriver;

public interface StepParams {

  WebDriver getWebDriver();
  KiteLogger getLogger();
  StepPhase getStepPhase();
  
}
