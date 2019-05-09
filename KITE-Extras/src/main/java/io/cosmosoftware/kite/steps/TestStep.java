package io.cosmosoftware.kite.steps;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.report.AllureStepReport;
import io.cosmosoftware.kite.report.Reporter;
import io.cosmosoftware.kite.report.Status;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.util.HashSet;
import java.util.Set;

import static io.cosmosoftware.kite.util.ReportUtils.getLogHeader;

public abstract class TestStep {
  
  
  protected final WebDriver webDriver;
  protected Logger logger = null;
  protected AllureStepReport report;
  private String name = getClassName();
  private boolean stepCompleted = false;
  
  public TestStep(WebDriver webDriver) {
    this.webDriver = webDriver;
  }
  
  public void execute() {
    try {
      logger.info("Executing step: " + stepDescription());
      step();
    } catch (Exception e) {
      Reporter.getInstance().processException(this.report, e);
    }
  }
  
  public void finish() {
    this.report.setStopTimestamp();
    stepCompleted = true;
  }
  
  private String getClassName() {
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
  
  public void init() {
    this.report = new AllureStepReport(getLogHeader(webDriver) + ": " + stepDescription());
    this.report.setDescription(stepDescription());
    this.report.setStartTimestamp();
  }
  
  public void setLogger(Logger logger) {
    this.logger = logger;
  }
  
  public void skip() {
    logger.warn("Skipping step: " + stepDescription());
    this.report.setStatus(Status.SKIPPED);
  }
  
  protected abstract void step();
  
  public boolean stepCompleted() {
    return this.stepCompleted;
  }
  
  public abstract String stepDescription();
  
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
  
}
