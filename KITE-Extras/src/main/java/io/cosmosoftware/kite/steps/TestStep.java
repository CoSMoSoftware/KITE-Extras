package io.cosmosoftware.kite.steps;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.report.AllureStepReport;
import io.cosmosoftware.kite.report.Reporter;
import io.cosmosoftware.kite.report.Status;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.openqa.selenium.WebDriver;

import static io.cosmosoftware.kite.util.ReportUtils.getLogHeader;

public abstract class TestStep {


  protected Logger logger = null;

  protected final WebDriver webDriver;
  protected AllureStepReport report;
  
  
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
  
  public void finish(){
    this.report.setStopTimestamp();
  }
  
  public AllureStepReport getStepReport() {
    return report;
  }
  
  public abstract String stepDescription();
  
  protected abstract void step() throws KiteTestException;

  public void setLogger(Logger logger) { this.logger = logger; }

}
