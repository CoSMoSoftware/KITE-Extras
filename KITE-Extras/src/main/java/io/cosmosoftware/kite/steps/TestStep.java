package io.cosmosoftware.kite.steps;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.report.AllureStepReport;
import io.cosmosoftware.kite.report.Reporter;
import io.cosmosoftware.kite.report.Status;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import static io.cosmosoftware.kite.util.ReportUtils.getLogHeader;

public abstract class TestStep {


  protected Logger logger = null;

  protected final WebDriver webDriver;
  protected AllureStepReport report;
  private boolean stepCompleted = false;

  private String name = getClassName();
  
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
  
  public void skip() {
    logger.warn("Skipping step: " + stepDescription());
    this.report.setStatus(Status.SKIPPED);
  }
  
  public void init(){
    this.report = new AllureStepReport(getLogHeader(webDriver) + ": " + stepDescription());
    this.report.setDescription(stepDescription());
    this.report.setStartTimestamp();
  }

  public boolean stepCompleted() {
    return this.stepCompleted;
  }
  
  public void finish(){
    this.report.setStopTimestamp();
    stepCompleted = true;
  }
  
  public AllureStepReport getStepReport() {
    return report;
  }
  
  public abstract String stepDescription();
  
  protected abstract void step() throws KiteTestException;

  public void setLogger(Logger logger) { this.logger = logger; }

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

}
