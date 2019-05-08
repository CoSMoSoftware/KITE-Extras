package io.cosmosoftware.kite.report;

import io.cosmosoftware.kite.exception.KiteTestException;
import org.apache.log4j.Logger;

import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.ArrayList;
import java.util.List;

import static io.cosmosoftware.kite.util.ReportUtils.getStackTrace;
import static io.cosmosoftware.kite.util.ReportUtils.timestamp;
import static io.cosmosoftware.kite.util.TestHelper.jsonToString;
import static io.cosmosoftware.kite.util.TestUtils.*;

public class Reporter {
  
  private static Reporter instance = new Reporter();
  private final String DEFAULT_REPORT_FOLDER = System.getProperty("user.dir") + "/kite-allure-reports/";
  protected Logger logger = Logger.getLogger(this.getClass().getName());
  private List<CustomAttachment> attachments = new ArrayList<>();
  private List<Container> containers = new ArrayList<>();
  private String reportPath = DEFAULT_REPORT_FOLDER;
  private List<AllureTestReport> tests = new ArrayList<>();

  private void addAttachment(AllureStepReport report, CustomAttachment attachment) {
    this.attachments.add(attachment);
    report.addAttachment(attachment);
  }
  
  public void addContainer(Container container) {
    this.containers.add(container);
  }
  
  public void addTest(AllureTestReport test) {
    this.tests.add(test);
  }
  
  public void clearLists() {
    this.containers = new ArrayList<>();
    this.tests = new ArrayList<>();
    this.attachments = new ArrayList<>();
  }
  
  public void generateReportFiles() {
    updateContainers();
    
    for (AllureTestReport test : tests) {
      String fileName = this.reportPath + test.getUuid() + "-result.json";
      printJsonTofile(test.toString(), fileName);
    }
    
    for (CustomAttachment attachment : attachments) {
      attachment.saveToFile(reportPath);
    }
  }
  
  public static Reporter getInstance() {
    return instance;
  }
  
  public void jsonAttachment(AllureStepReport report, String name, JsonValue jsonObject) {
    jsonAttachment(report, name, (JsonObject) jsonObject);
  }
  
  public void jsonAttachment(AllureStepReport report, String name, JsonObject jsonObject) {
    String value = jsonToString(jsonObject);
    CustomAttachment attachment = new CustomAttachment(name, "text/json", "json");
    attachment.setText(value);
    addAttachment(report, attachment);
  }
  
  public void processException(AllureStepReport report, Exception e) {
    StatusDetails details = new StatusDetails();
    Status status;
    String message;
    String trace = getStackTrace(e) + (e.getCause() != null ? "\r\nCaused by: " + getStackTrace(e.getCause()) : "");
    if (e instanceof KiteTestException) {
      details.setKnown(true);
      details.setMuted(((KiteTestException) e).isContinueOnFailure());
      report.setIgnore(((KiteTestException) e).isContinueOnFailure());
      status = ((KiteTestException) e).getStatus();
      message = e.getLocalizedMessage();
      if (report.canBeIgnore()) {
        logger.warn(
          "(Optional) Step " + status.value() + ":\r\n   message = " + message);
      } else {
        logger.error(
          "Step " + status.value() + ":\r\n   message = " + message);
      }
      logger.debug(trace);
    } else {
      message = "***UNHANDLED EXCEPTION*** \r\n This is a bug and must be fixed. The exception " +
        "must be caught and thrown as KiteTestException";
      details.setFlaky(true);
      status = Status.BROKEN;
      logger.error("Step " + status.value() + ":\r\n   message = " + message + "\r\n   trace = " + trace);
    }
    details.setMessage(message);
    details.setTrace(trace);
    report.setStatus(status);
    report.setDetails(details);
  }
  
  public void saveAttachmentToSubFolder(String name, String value, String type, String subFolder) {
    createDirs(this.reportPath + subFolder);
    printJsonTofile(value, verifyPathFormat(this.reportPath + subFolder) + name + "." + type);
  }
  
  public void screenshotAttachment(AllureStepReport report, byte[] screenshot) {
    CustomAttachment attachment = new CustomAttachment("Page-screenshot(" + timestamp() + ")", "image/png", "png");
    attachment.setScreenshot(screenshot);
    addAttachment(report, attachment);
  }
  
  public void screenshotAttachment(AllureStepReport report, String name, byte[] screenshot) {
    CustomAttachment attachment = new CustomAttachment(name, "image/png", "png");
    attachment.setScreenshot(screenshot);
    addAttachment(report, attachment);
  }
  
  public void setLogger(Logger logger) {
    this.logger = logger;
  }
  
  public void setReportPath(String reportPath) {
    if (reportPath != null && !reportPath.isEmpty()) {
      this.reportPath = verifyPathFormat(reportPath);
    }
    logger.info("Creating report folder if not exist at :" + this.reportPath);
    createDirs(this.reportPath);
  }
  
  public void textAttachment(AllureStepReport report, String name, String value, String type) {
    CustomAttachment attachment = new CustomAttachment(name, "text/" + type, type);
    attachment.setText(value);
    addAttachment(report, attachment);
  }
  
  public void updateContainers() {
    for (Container container : containers) {
      String fileName = this.reportPath + container.getUuid() + "-container.json";
      printJsonTofile(container.toString(), fileName);
    }
  }
  
}
