/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.report;

import io.cosmosoftware.kite.entities.Stage;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class ReportEntity {

  protected Reporter reporter;
  protected String name;
  protected String uuid;
  protected long start = 0;
  protected long stop = 0;
  protected String stage;


  protected ReportEntity(String name) {
    this.name = name;
    this.stage = Stage.SCHEDULED;
    this.uuid = UUID.randomUUID().toString();
  }

  public void setStartTimestamp() {
    setStartTimestamp(System.currentTimeMillis());
  }

  public void setStartTimestamp(long start) {
    this.stage = Stage.RUNNING;
    this.start = start;
  }

  public void setStopTimestamp() {
    setStopTimestamp(System.currentTimeMillis());
  }

  public void setStopTimestamp(long stop) {
    this.stage = Stage.FINISHED;
    this.stop = stop;
  }

  public String getUuid() {
    return uuid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  protected JsonObjectBuilder getJsonBuilder() {
    return Json.createObjectBuilder()
        .add("name", this.name)
        .add("start", this.start)
        .add("stop", this.stop)
        ;
  }

  public String getStage() {
    return stage;
  }

  public void setStage(String stage) {
    this.stage = stage;
  }

  public JsonObject toJson() {
    return this.getJsonBuilder().build();
  }

  @Override
  public String toString() {
    return toJson().toString();
  }

  /**
   * Sets reporter.
   *
   * @param reporter the reporter
   */
  public void setReporter(Reporter reporter) {
    this.reporter = reporter;
    if (this instanceof Container) {
      reporter.addContainer((Container) this);
    }
    if (this instanceof AllureTestReport) {
      reporter.addTest((AllureTestReport) this);
    }
  }
}
