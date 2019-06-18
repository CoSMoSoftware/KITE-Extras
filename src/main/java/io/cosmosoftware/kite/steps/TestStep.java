/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.steps;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.report.AllureStepReport;
import io.cosmosoftware.kite.report.KiteLogger;
import io.cosmosoftware.kite.report.Reporter;
import io.cosmosoftware.kite.report.Status;
import org.openqa.selenium.WebDriver;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import static io.cosmosoftware.kite.steps.StepPhase.DEFAULT;
import static io.cosmosoftware.kite.util.ReportUtils.getLogHeader;
import static io.cosmosoftware.kite.util.ReportUtils.saveScreenshotPNG;

public abstract class TestStep {
  
  
  protected final WebDriver webDriver;
  protected KiteLogger logger = null;
  protected AllureStepReport report;
  private String name = getClassName();
  private boolean stepCompleted = false;

  private boolean optional = false;

  private StepPhase stepPhase = DEFAULT;
  private StepPhase currentStepPhase = DEFAULT;

  private LinkedHashMap<String, String> csvResult = null;

  public TestStep(WebDriver webDriver) {
    this.webDriver = webDriver;
  }
  
  public TestStep(WebDriver webDriver, StepPhase stepPhase) {
    this.webDriver = webDriver;
    this.stepPhase = stepPhase;
  }
  
  public void execute() {
    try {
      logger.info(currentStepPhase.getShortName() + "Executing step: " + stepDescription());
      step();
    } catch (Exception e) {
      String screenshotName = "error_screenshot_"+this.getName();
      try {
        Reporter.getInstance().screenshotAttachment(this.report, screenshotName, saveScreenshotPNG(webDriver));
      } catch (KiteTestException ex) {
        logger.warn("Could not attach screenshot to error of step: " + stepDescription());
      }
      Reporter.getInstance().processException(this.report, e, optional);
    }
  }
  
  public void finish() {
    this.report.setStopTimestamp();
    stepCompleted = true;
  }
  
  public String getClassName() {
    String s = this.getClass().getSimpleName();
    if (s.contains(".")) {
      s = s.substring(s.lastIndexOf(".") + 1);
    }
    return s;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public AllureStepReport getStepReport() {
    return report;
  }
  
  public void init(StepPhase stepPhase) {
    this.currentStepPhase = stepPhase;
    this.report = new AllureStepReport(getClientID() + ": " + stepDescription());
    this.report.setDescription(currentStepPhase.getShortName() + stepDescription());
    this.report.setStartTimestamp();
  }
  
  public String getClientID() {
    return currentStepPhase.getShortName() + getLogHeader(webDriver);
  }
  
  public void setLogger(KiteLogger logger) {
    this.logger = KiteLogger.getLogger(logger, getClientID() + ": ");
  }
  
  public void skip() {
    logger.warn(currentStepPhase.getShortName() + "Skipping step: " + stepDescription());
    this.report.setStatus(Status.SKIPPED);
  }
  
  protected abstract void step() throws KiteTestException;
  
  public boolean stepCompleted() {
    return this.stepCompleted;
  }
  
  public abstract String stepDescription();

  public StepPhase getStepPhase() {
    return stepPhase;
  }

  public void setStepPhase(StepPhase stepPhase) {
    this.stepPhase = stepPhase;
  }
  
  protected String translateClassName() {
    
    String name = this.getClass().getSimpleName();
    Set<String> upperLetters = new HashSet<>();
    
    for (char letter : name.toCharArray()) {
      String letterString = Character.toString(letter);
      if (letterString.matches("[A-Z]") || letterString.matches("[0-9]")) {
        upperLetters.add(letterString);
      }
    }
    
    for (String letterString : upperLetters) {
      name = name.replaceAll(letterString, " " + letterString.toLowerCase());
    }
    
    return name;
  }
  

  public void setCsvResult(LinkedHashMap<String, String> csvResult) {
    this.csvResult = csvResult;
  }

  public LinkedHashMap<String, String> getCsvResult() {
    return csvResult;
  }

  public void addToCsvResult(String key, String value) {
    if (this.csvResult == null) {
      this.csvResult = new LinkedHashMap<>();
    }
    this.csvResult.put(key, value);
  }

  public void setOptional(boolean optional) {
    this.optional = optional;
  }
}
