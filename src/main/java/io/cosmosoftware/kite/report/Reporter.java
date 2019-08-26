/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.report;

import io.cosmosoftware.kite.exception.KiteTestException;

import javax.json.JsonObject;
import javax.json.JsonValue;
import java.io.*;
import java.util.*;

import static io.cosmosoftware.kite.report.CSVHelper.jsonToString;
import static io.cosmosoftware.kite.util.ReportUtils.getStackTrace;
import static io.cosmosoftware.kite.util.ReportUtils.timestamp;
import static io.cosmosoftware.kite.util.ReportUtils.zipFile;
import static io.cosmosoftware.kite.util.TestUtils.*;

/**
 * The type Reporter.
 */
public class Reporter {

  private final String DEFAULT_REPORT_FOLDER =
      System.getProperty("user.dir") + "/kite-allure-reports/";
  protected KiteLogger logger = KiteLogger.getLogger(this.getClass().getName());
  private final Map<String, CSVHelper> csvWriterMap = new HashMap();
  private final String testName;
  private boolean csvReport = false;
  private String reportPath = DEFAULT_REPORT_FOLDER;
  private String timestamp = timestamp();
  private List<CustomAttachment> attachments = Collections.synchronizedList(new ArrayList<>());
  private List<Container> containers = Collections.synchronizedList(new ArrayList<>());
  private List<AllureTestReport> tests = Collections.synchronizedList(new ArrayList<>());

  
  /**
   * Instantiates a new Reporter.
   */
  public Reporter(String testName) {
    this.testName = testName;
  }

  public void setCsvReport(boolean csvReport) {
    this.csvReport = csvReport;
  }

  public boolean csvReport() {
    return this.csvReport;
  }

  public String getTimestamp() {
    return timestamp;
  }

  private void addAttachment(AllureStepReport report, CustomAttachment attachment) {
    report.addAttachment(attachment);
    this.attachments.add(attachment);
  }

  /**
   * Add container.
   *
   * @param container the container
   */
  public void addContainer(Container container) {
    this.containers.add(container);
  }

  /**
   * Add test.
   *
   * @param test the test
   */
  public void addTest(AllureTestReport test) {
    this.tests.add(test);
  }

  /**
   * Clear lists.
   */
  public void clearLists() {
    this.containers = Collections.synchronizedList(new ArrayList<>());;
    this.tests = Collections.synchronizedList(new ArrayList<>());;
    this.attachments = Collections.synchronizedList(new ArrayList<>());;
  }

  /**
   * Generate report files.
   */
  public void generateReportFiles() {
    updateContainers();
    generateCategoryJsonFile();
    for (AllureTestReport test : tests) {
      test.generateReport();
    }

    for (CustomAttachment attachment : attachments) {
      attachment.saveToFile(reportPath);
    }
    if (this.csvReport) {
      closeCSVWriter();
    }
    // zipFile(this.reportPath, this.reportPath + "report.zip");
  }

  /**
   * Json attachment.
   *
   * @param report the report
   * @param name the name
   * @param jsonObject the json object
   */
  public void jsonAttachment(AllureStepReport report, String name, JsonValue jsonObject) {
    jsonAttachment(report, name, (JsonObject) jsonObject);
  }

  /**
   * Json attachment.
   *
   * @param report the report
   * @param name the name
   * @param jsonObject the json object
   */
  public void jsonAttachment(AllureStepReport report, String name, JsonObject jsonObject) {
    String value = jsonToString(jsonObject);
    CustomAttachment attachment = new CustomAttachment(name, "text/json", "json");
    attachment.setText(value);
    addAttachment(report, attachment);
    if (!attachment.getName().contains("payload") && csvReport) {
      updateCSVReport(report, attachment);
    }
  }

  
  private synchronized void updateCSVReport(AllureStepReport report, CustomAttachment attachment) {
    String attachmentName = report.getPhase().getShortName().trim() + attachment.getName();
    if (!csvWriterMap.keySet().contains(attachmentName)) {
      String CSVFileName = attachmentName + "_"
        +  this.timestamp + ".csv";
      csvWriterMap.put(attachmentName, new CSVHelper(CSVFileName));
    }
    csvWriterMap.get(attachmentName).println(attachment.getJsonText(), this.reportPath + "csv-report/" + testName + "/", report.getClientId());
  }
  
  private void closeCSVWriter() {
    for (String attachmentName : csvWriterMap.keySet()) {
      csvWriterMap.get(attachmentName).close();
    }
  }
  
  /**
   * Process exception.
   *
   * @param report the report
   * @param e the e
   * @param optional the optional
   */
  public void processException(AllureStepReport report, Exception e, boolean optional) {
    StatusDetails details = new StatusDetails();
    Status status;
    String message;
    String trace =
        getStackTrace(e) + (e.getCause() != null ? "\r\nCaused by: " + getStackTrace(e.getCause())
            : "");
    if (e instanceof KiteTestException) {
      details.setKnown(true);
      details.setMuted(((KiteTestException) e).isContinueOnFailure());
      report.setIgnore(((KiteTestException) e).isContinueOnFailure() || optional);
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
      report.setIgnore(optional);
      details.setFlaky(true);
      status = Status.BROKEN;
      logger.error(
          "Step " + status.value() + ":\r\n   message = " + message + "\r\n   trace = " + trace);
    }
    details.setMessage(message);
    details.setTrace(trace);
    report.setStatus(status);
    report.setDetails(details);
    jsonAttachment(report, "error.log", details.toJson());
  }

  /**
   * Save attachment to sub folder.
   *
   * @param name the name
   * @param value the value
   * @param type the type
   * @param subFolder the sub folder
   */
  public void saveAttachmentToSubFolder(String name, String value, String type, String subFolder) {
    createDirs(this.reportPath + subFolder);
    printJsonTofile(value, verifyPathFormat(this.reportPath + subFolder) + name + "." + type);
  }

  /**
   * Screenshot attachment.
   *
   * @param report the report
   * @param screenshot the screenshot
   */
  public void screenshotAttachment(AllureStepReport report, byte[] screenshot) {
    CustomAttachment attachment = new CustomAttachment("Page-screenshot(" + timestamp() + ")",
        "image/png", "png");
    attachment.setScreenshot(screenshot);
    addAttachment(report, attachment);
  }

  /**
   * Screenshot attachment.
   *
   * @param report the report
   * @param name the name
   * @param screenshot the screenshot
   */
  public void screenshotAttachment(AllureStepReport report, String name, byte[] screenshot) {
    CustomAttachment attachment = new CustomAttachment(name, "image/png", "png");
    attachment.setScreenshot(screenshot);
    addAttachment(report, attachment);
  }

  /**
   * Sets logger.
   *
   * @param logger the logger
   */
  public void setLogger(KiteLogger logger) {
    this.logger = logger;
  }

  /**
   * Text attachment.
   *
   * @param report the report
   * @param name the name
   * @param value the value
   * @param type the type
   */
  public void textAttachment(AllureStepReport report, String name, String value, String type) {
    CustomAttachment attachment = new CustomAttachment(name, "text/" + type, type);
    attachment.setText(value);
    addAttachment(report, attachment);
  }

  /**
   * Gets report path.
   *
   * @return the report path
   */
  public String getReportPath() {
    return reportPath;
  }

  /**
   * Sets report path.
   *
   * @param reportPath the report path
   */
  public void setReportPath(String reportPath) {
    if (reportPath != null && !reportPath.isEmpty()) {
      this.reportPath = verifyPathFormat(reportPath);
    }
    logger.info("Creating report folder if not exist at :" + this.reportPath);
  }

  /**
   * Update containers.
   */
  public synchronized void updateContainers() {
    createDirs(this.reportPath);
    for (Container container : containers) {
      String fileName = this.reportPath + container.getUuid() + "-container.json";
      printJsonTofile(container.toString(), fileName);
    }
  }

  private void generateCategoryJsonFile(){
    File file = new File(this.reportPath + "categories.json");
    if (!file.exists()) {
      BufferedWriter writer = null;
      try {
        // Writes bytes from the specified byte array to this file output stream
        writer = new BufferedWriter(new FileWriter(file));
        writer.write(defaultCategoriesString());

      }
      catch (FileNotFoundException e) {
        logger.error("File not found" + e);
      } catch (IOException ioe) {
        logger.error("Exception while writing file " + ioe);
      } finally {
        // close the streams using close method
        try {
          if (writer != null) {
            writer.close();
          }
        } catch (IOException ioe) {
          logger.error("Error while closing stream: " + ioe);
        }
      }
    }
  }

  private String defaultCategoriesString() {
    return "["
          + "{"
            + "\"name\":\"Connection Issues\","
            + "\"matchedStatuses\":[\"failed\"],"
            + "\"messageRegex\":\".*onnection.*\""
          + "},"
          + "{"
            + "\"name\":\"Video display issues\","
            + "\"matchedStatuses\":[\"failed\"],"
            + "\"messageRegex\":\".*video.*\""
          + "},"
          + "{"
            + "\"name\":\"Audio issues\","
            + "\"matchedStatuses\":[\"failed\"],"
            + "\"messageRegex\":\".*audio.*\""
          + "},"
          + "{"
            + "\"name\":\"Get stats issues\","
            + "\"matchedStatuses\":[\"failed\"],"
            + "\"messageRegex\":\".*tats.*\""
          + "}"
        + "]";
  }

}
