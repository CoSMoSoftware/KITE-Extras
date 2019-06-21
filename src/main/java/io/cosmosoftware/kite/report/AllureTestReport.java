/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.report;

import java.util.ArrayList;
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
    super(name);
    this.labels = new ParamList();
    this.links = new ArrayList<>();
    this.historyId = UUID.randomUUID().toString();
    reporter.addTest(this);
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
}
