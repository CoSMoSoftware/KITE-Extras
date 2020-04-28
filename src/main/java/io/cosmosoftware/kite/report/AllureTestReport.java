/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.report;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static io.cosmosoftware.kite.util.TestUtils.printJsonTofile;

public class AllureTestReport extends AllureStepReport {

  private String historyId;
  private String fullName;
  private ParamList labels;
  private List<String> links;
  private List<Integer> testClientMatrix = new ArrayList<>();

  /**
   * Instantiates a new AllureStepReport report.
   *
   * @param name the name
   */
  public AllureTestReport(String name) {
    super(null , name);
    this.labels = new ParamList();
    this.links = Collections.synchronizedList(new ArrayList<>());;
    this.historyId = UUID.randomUUID().toString();
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public void addLabel(String name, String value) {
    this.labels.put(name, value);
  }

  public String getLabel(String name) {
    return this.labels.get(name);
  }

  public void addLink(String link) {
    this.links.add(link);
  }

  @Override
  public JsonObjectBuilder getJsonBuilder() {
    JsonArrayBuilder linkArray = Json.createArrayBuilder();
    for (String link : links) {
      linkArray.add(link);
    }

    return super.getJsonBuilder()
        .add("uuid", this.uuid)
        .add("fullName", fullName)
        .add("status", this.getActualStatus().toString())
        .add("historyId", historyId)
        .add("links", linkArray)
        .add("labels", labels.toJson())
        ;
  }

  public void generateReport() {
    this.fixWrongStatusDetails();
    String fileName = this.reporter.getReportPath() + this.uuid + "-result.json";
    printJsonTofile(this.toString(), fileName);
  }

  public void setTestClientMatrix(List<Integer> testClientMatrix) {
    this.testClientMatrix = testClientMatrix;
  }

  public void addClientIndex(int clientIndex) {
    this.testClientMatrix.add(clientIndex);
  }

  public List<Integer> getTestClientMatrix() {
    return testClientMatrix;
  }

  private void fixWrongStatusDetails() {
//    if (!status.equals(Status.PASSED) && details.getMessage().equals("The test has passed successfully!")) {
      for (AllureStepReport step : steps) {
        if (!step.getStatus().equals(Status.PASSED)) {
          if (!step.getDetails().getMessage().contains("passed successfully")) {
            this.details = step.getDetails();
            break;
          }
        }
      }
//    }
  }

}
