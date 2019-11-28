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

public class Container extends ReportEntity {

  private List<String> childrenId = Collections.synchronizedList(new ArrayList<>());
  private List<AllureStepReport> befores = Collections.synchronizedList(new ArrayList<>());
  private List<AllureStepReport> afters = Collections.synchronizedList(new ArrayList<>());
  private String parentSuite;

  public Container(String name) {
    super(name);
    this.setStartTimestamp();
  }

  public void setParentSuite(String parentSuite) {
    this.parentSuite = parentSuite;
  }

  public String getParentSuite() {
    return parentSuite;
  }

  public void addChild(String childId) {
    this.childrenId.add(childId);
  }

  public void removeChild(String childId) {
    this.childrenId.remove(childId);
  }

  public void addBeforeStep(AllureStepReport step) {
    this.befores.add(step);
  }

  public void addAfterStep(AllureStepReport step) {
    this.afters.add(step);
  }


  @Override
  protected JsonObjectBuilder getJsonBuilder() {
    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    for (String childId : childrenId) {
      arrayBuilder.add(childId);
    }
    JsonArrayBuilder beforesArray = Json.createArrayBuilder();
    for (AllureStepReport before : befores) {
      beforesArray.add(before.getJsonBuilder());
    }
    JsonArrayBuilder aftersArray = Json.createArrayBuilder();
    for (AllureStepReport after : afters) {
      aftersArray.add(after.getJsonBuilder());
    }

    return super.getJsonBuilder()
        .add("uuid", this.uuid)
        .add("children", arrayBuilder)
        .add("befores", beforesArray)
        .add("afters", aftersArray)
        ;
  }

}
