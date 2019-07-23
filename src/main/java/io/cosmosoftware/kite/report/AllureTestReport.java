/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.report;

import static io.cosmosoftware.kite.util.TestUtils.printJsonTofile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

public class AllureTestReport extends AllureStepReport {

  private String historyId;
  private String fullName;
  private ParamList labels;
  private List<String> links;

  /**
   * Instantiates a new AllureStepReport report.
   *
   * @param name the name
   */
  public AllureTestReport(String name) {
    super(null , name);
    this.labels = new ParamList();
    this.links = Collections.synchronizedList(new ArrayList<>());;
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
        .add("historyId", historyId)
        .add("links", linkArray)
        .add("labels", labels.toJson())
        ;
  }

  public void generateReport() {
    String fileName = this.reporter.getReportPath() + this.uuid + "-result.json";
    printJsonTofile(this.toString(), fileName);
    this.reporter.updateContainers();
  }

  public void setHistoryId(String historyId) {
    this.historyId = historyId;
  }

  public String getFullName() {
    return fullName;
  }

  @Override
  public void setStopTimestamp() {
    super.setStopTimestamp();
    this.generateReport();
  }


}
