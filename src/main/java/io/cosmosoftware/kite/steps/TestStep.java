/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.steps;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
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

/**
 * The type Test step.
 */
public abstract class TestStep {

  protected WebDriver webDriver;
  protected KiteLogger logger;
  protected Reporter reporter;
  protected Runner runner;
  protected AllureStepReport report;
  protected Status status = Status.PENDING;
  private String name = this.getClass().getSimpleName();
  private boolean stepCompleted = false;
  private boolean screenShotOnFailure = true;

  private boolean optional = false;
  private boolean ignoreBroken = false;
  private boolean silent = false;
  private boolean neverSkip = false;

  private StepPhase stepPhase = DEFAULT;
  private StepPhase currentStepPhase = DEFAULT;
  private String clientName;

  private LinkedHashMap<String, String> csvResult = null;

  private TestStep dependOn;
  /**
   * Instantiates a new Test step.
   *
   * @param runner the runner
   */
  public TestStep(Runner runner) {
    this.setRunner(runner);
  }

  /**
   * Instantiates a new Test step.
   *
   */
  public TestStep() {
  }

  public void setRunner(Runner runner) {
    this.runner = runner;
    this.webDriver = runner.getWebDriver();
    this.stepPhase = runner.getStepPhase();
    this.reporter = runner.getReporter();
    this.clientName = runner.getClientName();
    this.logger = KiteLogger.getLogger(runner.getLogger(), getClientID() + ": ");
  }

  /**
   * Execute.
   */
  public void execute() {
    try {
      step();
    } catch (Exception e) {
      //force silent to false in case of error, so the failure appears in the report in all cases.
      silent = false;
      if (screenShotOnFailure) {
        try {
          String screenshotName = "error_screenshot_" + this.getName();
          reporter.screenshotAttachment(this.report, screenshotName, saveScreenshotPNG(webDriver));
        } catch (KiteTestException ex) {
          logger.warn("Could not attach screenshot to error of step: " + stepDescription());
        }
      }
      reporter.processException(this.report, e, optional);
    }
    if (!this.getClass().equals(GetSystemInfoStep.class)) {
      new GetSystemInfoStep(this.runner).processTestStep(this.stepPhase, this.report, false);
    }
    this.status = this.report.getStatus();
    logger.info(getStatusString() + currentStepPhase.getShortName()  + stepDescription() );
  }

  public String getStatusString() {
    return " [" + status + "] ";
  }

  public Status getStatus() {
    return status;
  }

  /**
   * Finish.
   */
  public void finish() {
    this.report.setStopTimestamp();
    stepCompleted = true;
  }

  /**
   * Gets class name.
   *
   * @return the class name
   */
  public String getClassName() {
    return this.name;
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets name.
   *
   * @param name the name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets step report.
   *
   * @return the step report
   */
  public AllureStepReport getStepReport() {
    return report;
  }

  /**
   * Init.
   *
   * @param stepPhase the step phase
   */
  public void init(StepPhase stepPhase) {
    this.currentStepPhase = stepPhase;
    this.report = new AllureStepReport(getClientID(), stepDescription());
    this.report.setReporter(this.reporter);
    this.report.setDescription(currentStepPhase.getShortName() + stepDescription());
    this.report.setStartTimestamp();
    this.report.setPhase(stepPhase);
  }

  /**
   * Gets client id.
   *
   * @return the client id
   */
  public String getClientID() {    
    return currentStepPhase.getShortName() + getLogHeader(this.webDriver)
        + (this.clientName != null && this.clientName.length() > 0 ? (" - " + this.clientName) : "");
  }

  /**
   * Skip.
   */
  public void skip() {
    logger.warn(currentStepPhase.getShortName() + "[SKIPPING] " + stepDescription());
    this.report.setStatus(Status.SKIPPED);
  }

  /**
   * Step.
   *
   * @throws KiteTestException the kite test exception
   */
  protected abstract void step() throws KiteTestException;

  /**
   * Step completed boolean.
   *
   * @return the boolean
   */
  public boolean stepCompleted() {
    return this.stepCompleted;
  }

  /**
   * Step description string.
   *
   * @return the string
   */
  public abstract String stepDescription();

  /**
   * Gets step phase.
   *
   * @return the step phase
   */
  public StepPhase getStepPhase() {
    return stepPhase;
  }

  /**
   * Sets step phase.
   *
   * @param stepPhase the step phase
   */
  public void setStepPhase(StepPhase stepPhase) {
    this.stepPhase = stepPhase;
  }

  /**
   * Translate class name string.
   *
   * @return the string
   */
  protected String translateClassName() {
    String res = name;
    Set<String> upperLetters = new HashSet<>();

    for (char letter : res.toCharArray()) {
      String letterString = Character.toString(letter);
      if (letterString.matches("[A-Z]") || letterString.matches("[0-9]")) {
        upperLetters.add(letterString);
      }
    }
    for (String letterString : upperLetters) {
      res = res.replaceAll(letterString, " " + letterString.toLowerCase());
    }

    return res;
  }

  /**
   * Gets csv result.
   *
   * @return the csv result
   */
  public LinkedHashMap<String, String> getCsvResult() {
    return csvResult;
  }

  /**
   * Sets csv result.
   *
   * @param csvResult the csv result
   */
  public void setCsvResult(LinkedHashMap<String, String> csvResult) {
    this.csvResult = csvResult;
  }

  /**
   * Add to csv result.
   *
   * @param key the key
   * @param value the value
   */
  public void addToCsvResult(String key, String value) {
    if (this.csvResult == null) {
      this.csvResult = new LinkedHashMap<>();
    }
    this.csvResult.put(key, value);
  }

  /**
   * Sets optional.
   *
   * @param optional the optional
   */
  public void setOptional(boolean optional) {
    this.optional = optional;
  }

  /**
   * 
   * @return true if this step is silent
   */
  public boolean isSilent() {
    return silent;
  }

  /**
   * Sets the step to be silent (no report generated)
   *
   * @param silent 
   */
  public void setSilent(boolean silent) {
    this.silent = silent;
  }
  


  /**
   * Process the step in the new TestRunner and Kite
   *
   * @param stepPhase the StepPhase for this stepExecution
   * @param parentStepReport the report of the parent step, containing the status of the last step.
   * @param loadTest true if this is a load test.
   */
  public void processTestStep(StepPhase stepPhase, AllureStepReport parentStepReport, boolean loadTest) {
    if (loadTest && !stepPhase.shouldProcess(this)) {
      logger.debug("Skipping " + this.getClassName() + " (only execute in " + this.getStepPhase() + ")");
      return;
    }
    this.init(stepPhase);
    if (this.canProceed()) {
      if (parentStepReport != null) {
        if (!parentStepReport.failed() && !parentStepReport.broken()) {
          this.execute();
        } else {
          if (parentStepReport.canBeIgnore() || this.neverSkip) {
            this.execute();
          } else {
            logger.debug("parent step failed, and can't be ignored, skipping " + this.name);
            this.skip();
          }
        }
      } else {
        this.execute();
      }
    } else {
      logger.debug("Condition step was not met, skipping " + this.name);
      this.skip();
    }
    this.finish();
    if (!this.isSilent()) {
        if (this.report.getStatus().equals(Status.BROKEN)) {
          if (!this.ignoreBroken()) {
            addToParentStep(parentStepReport);
          }
        } else {
          addToParentStep(parentStepReport);
        }
    }
  }

  public void skipTestStep (StepPhase stepPhase, AllureStepReport parentStepReport, boolean loadTest) {
    this.init(stepPhase);
    this.skip(); // this is skipped by force, so no consideration for neverSkip
    this.finish();
    if (!this.isSilent()) {
      addToParentStep(parentStepReport);
    }
  }

  public void setScreenShotOnFailure(boolean screenShotOnFailure) {
    this.screenShotOnFailure = screenShotOnFailure;
  }

  public void setNeverSkip(boolean neverSkip) {
    this.neverSkip = neverSkip;
  }

  private void addToParentStep (AllureStepReport parentStepReport) {
    if (parentStepReport != null) {
      parentStepReport.addStepReport(this.getStepReport());
    }
  }

  public void setDependOn(TestStep dependOn) {
    this.dependOn = dependOn;
  }

  public boolean canProceed() {
    if (this.dependOn == null) {
      return true;
    }
    return this.dependOn.getStatus().equals(Status.PASSED);
  }

  public void setIgnoreBroken(boolean ignoreBroken) {
    this.ignoreBroken = ignoreBroken;
  }

  public boolean ignoreBroken() {
    return ignoreBroken;
  }

  public void setReporter(Reporter reporter) {
    this.reporter = reporter;
  }

  public Reporter getReporter() {
    return reporter;
  }

}
