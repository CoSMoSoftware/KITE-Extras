/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.report;

import static io.cosmosoftware.kite.report.CSVHelper.jsonToString;
import static io.cosmosoftware.kite.util.ReportUtils.getStackTrace;
import static io.cosmosoftware.kite.util.ReportUtils.timestamp;
import static io.cosmosoftware.kite.util.TestUtils.createDirs;
import static io.cosmosoftware.kite.util.TestUtils.printJsonTofile;
import static io.cosmosoftware.kite.util.TestUtils.readJsonFile;
import static io.cosmosoftware.kite.util.TestUtils.verifyPathFormat;

import io.cosmosoftware.kite.exception.KiteTestException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

/**
 * The type Reporter.
 */
public class Reporter {

  private final String DEFAULT_REPORT_FOLDER =
      System.getProperty("user.dir") + "/kite-allure-reports/";
  private final Map<String, CSVHelper> csvWriterMap = new HashMap();
  private final String testName;
  protected KiteLogger logger = KiteLogger.getLogger(this.getClass().getName());
  private boolean csvReport = false;
  private String reportPath = DEFAULT_REPORT_FOLDER;
  private String retryPath = reportPath + "retry-config/";
  private String configFilePath;
  private JsonObject testConfig;
  private long startTime = System.currentTimeMillis();
  private String timestamp = timestamp();
  private Environment environment = new Environment();
  private List<CustomAttachment> attachments = Collections.synchronizedList(new ArrayList<>());
  private List<Container> containers = Collections.synchronizedList(new ArrayList<>());
  private List<AllureTestReport> tests = Collections.synchronizedList(new ArrayList<>());
  private List<Category> categories = new ArrayList<>();
  private List<String> failedClientMatrixList = new ArrayList<>();
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

  private synchronized void addAttachment(AllureStepReport report, CustomAttachment attachment) {
    report.addAttachment(attachment);
    this.attachments.add(attachment);
  }

  /**
   * Add container.
   *
   * @param container the container
   */
  public synchronized void addContainer(Container container) {
    this.containers.add(container);
  }

  /**
   * Add test.
   *
   * @param test the test
   */
  public synchronized void addTest(AllureTestReport test) {
    this.tests.add(test);
  }

  /**
   * Clear lists.
   */
  public synchronized void clearLists() {
    this.containers = Collections.synchronizedList(new ArrayList<>());
    ;
    this.tests = Collections.synchronizedList(new ArrayList<>());
    ;
    this.attachments = Collections.synchronizedList(new ArrayList<>());
    ;
  }

  /**
   * Generate report files.
   */
  public synchronized void generateReportFiles() {
    updateContainers();
    for (AllureTestReport test : tests) {
      test.generateReport();
      if (!test.getStatus().equals(Status.PASSED)) {
        failedClientMatrixList.add(test.getTestClientMatrix());
        logger.warn("Adding " + test.getTestClientMatrix() + " to retry list..");
      }
    }
    generateFile("environment.properties", this.environment.toString());
    generateFile("categories.json", generateCategories());
    if (generateRetryConfigFile() != null) {
      logger.warn("Done! Some test cases might need to be rerun!");
      generateFile(retryPath + this.testConfig.getString("name")
              .replaceAll(" ", "_").replaceAll(":","_") + "-" + timestamp,
          generateRetryConfigFile());
    } else {
      logger.warn("Done! All test cases have passed!");
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
  public synchronized void jsonAttachment(AllureStepReport report, String name,
      JsonValue jsonObject) {
    jsonAttachment(report, name, (JsonObject) jsonObject);
  }

  /**
   * Json attachment.
   *
   * @param report the report
   * @param name the name
   * @param jsonObject the json object
   */
  public synchronized void jsonAttachment(AllureStepReport report, String name,
      JsonObject jsonObject) {
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
          + this.timestamp + ".csv";
      csvWriterMap.put(attachmentName, new CSVHelper(CSVFileName));
    }
    csvWriterMap.get(attachmentName)
        .println(attachment.getJsonText(), this.reportPath + "csv-report/" + testName + "/",
            report.getClientId());
  }

  private synchronized void closeCSVWriter() {
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
  public synchronized void processException(AllureStepReport report, Exception e,
      boolean optional) {
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
  public synchronized void saveAttachmentToSubFolder(String name, String value, String type,
      String subFolder) {
    createDirs(this.reportPath + subFolder);
    printJsonTofile(value, verifyPathFormat(this.reportPath + subFolder) + name + "." + type);
  }

  /**
   * Screenshot attachment.
   *
   * @param report the report
   * @param screenshot the screenshot
   */
  public synchronized void screenshotAttachment(AllureStepReport report, byte[] screenshot) {
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
  public synchronized void screenshotAttachment(AllureStepReport report, String name,
      byte[] screenshot) {
    CustomAttachment attachment = new CustomAttachment(name, "image/png", "png");
    attachment.setScreenshot(screenshot);
    addAttachment(report, attachment);
  }

  /**
   * Sets logger.
   *
   * @param logger the logger
   */
  public synchronized void setLogger(KiteLogger logger) {
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
  public synchronized void textAttachment(AllureStepReport report, String name, String value,
      String type) {
    CustomAttachment attachment = new CustomAttachment(name, "text/" + type, type);
    attachment.setText(value);
    addAttachment(report, attachment);
  }

  /**
   * Gets report path.
   *
   * @return the report path
   */
  public synchronized String getReportPath() {
    return reportPath;
  }

  /**
   * Sets report path.
   *
   * @param reportPath the report path
   */
  public synchronized void setReportPath(String reportPath) {
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
    createDirs(this.retryPath);
    for (Container container : containers) {
      String fileName = this.reportPath + container.getUuid() + "-container.json";
      printJsonTofile(container.toString(), fileName);
    }
  }

  private synchronized String generateCategories() {
    addDefaultCategories();
    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    for (Category category : categories) {
      arrayBuilder.add(category.toJson());
    }
    return arrayBuilder.build().toString();
  }

  private synchronized void addDefaultCategories() {
    Category category1 = new Category("Passed Tests");
    category1.addStatus("passed");
    Category category2 = new FailedCategory("Connection Issues");
    category2.setMessageRegex("onnection");
    Category category3 = new FailedCategory("WebDriver spawning Issues");
    category3.setMessageRegex("river");
    Category category4 = new FailedCategory("Video Issues");
    category4.setMessageRegex("ideo");
    Category category5 = new FailedCategory("Audio Issues");
    category5.setMessageRegex("udio");
    Category category6 = new FailedCategory("Get Stats Issues");
    category6.setMessageRegex("tats");
    Category category7 = new Category("WebDriver Unexpected Issues");
    category7.addStatus("broken");
    category7.setMessageRegex("river");

    addCategory(category1);
    addCategory(category2);
    addCategory(category3);
    addCategory(category4);
    addCategory(category5);
    addCategory(category6);
    addCategory(category7);
  }

  public synchronized void addCategory(Category category) {
    this.categories.add(category);
  }

  public synchronized void addCategory(List<Category> categories) {
    this.categories.addAll(categories);
  }

  public synchronized void addEnvironmentParam(String key, String value) {
    this.environment.put(key, value);
  }

  private void generateFile(String fileName, String content) {
    File file = new File(this.reportPath + fileName);
    if (!file.exists()) {
      BufferedWriter writer = null;
      try {
        // Writes bytes from the specified byte array to this file output stream
        writer = new BufferedWriter(new FileWriter(file));
        writer.write(content);

      } catch (FileNotFoundException e) {
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

  private String generateRetryConfigFile() {
    if (this.configFilePath != null
        && !this.configFilePath.isEmpty()
        && !failedClientMatrixList.isEmpty()) {
      JsonObject configFileObject = readJsonFile(configFilePath);
      JsonObjectBuilder builder = Json.createObjectBuilder();
      builder.add("name", configFileObject.get("name"));
      builder.add("grids", configFileObject.get("grids"));
      builder.add("clients", configFileObject.get("clients"));
      if (configFileObject.get("cloud")!=null) {
        builder.add("cloud", configFileObject.get("cloud"));
      }
      if (configFileObject.get("permute")!=null) {
        builder.add("permute", configFileObject.get("permute"));
      }
      if (configFileObject.get("networkInstrumentation")!=null) {
        builder.add("networkInstrumentation", configFileObject.get("networkInstrumentation"));
      }
      if (testConfig != null) {
        builder.add("tests", Json.createArrayBuilder().add(testConfig));
      }
      JsonArrayBuilder retryArrayBuilder = Json.createArrayBuilder();
      for (String tuple : failedClientMatrixList) {
        retryArrayBuilder.add(tuple);
      }
      builder.add("matrix", retryArrayBuilder);
      return builder.build().toString();
    }
    return null;
  }

  public void setConfigFilePath(String configFilePath) {
    this.configFilePath = configFilePath;
  }

  public void setTestConfig(JsonObject testConfig) {
    this.testConfig = testConfig;
  }

  public long getStartTime() {
    return startTime;
  }

  public Environment getEnvironment() {
    return environment;
  }
}
