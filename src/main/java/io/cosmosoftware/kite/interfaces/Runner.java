package io.cosmosoftware.kite.interfaces;

import io.cosmosoftware.kite.report.KiteLogger;
import io.cosmosoftware.kite.steps.StepPhase;
import org.openqa.selenium.WebDriver;


/**
 * Interface for KITE Framework's TestRunner class.
 */
public interface Runner {

  WebDriver getWebDriver();

  KiteLogger getLogger();

  StepPhase getStepPhase();

}
