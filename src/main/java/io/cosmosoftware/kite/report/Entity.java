/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.report;

import io.cosmosoftware.kite.entities.Stage;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class Entity {

  protected String name;
  protected String uuid;
  protected long start = 0;
  protected long stop = 0;
  protected String stage;


  protected Entity(String name) {
    this.name = name;
    this.stage = Stage.SCHEDULED;
    this.uuid = UUID.randomUUID().toString();
  }

  public void setStartTimestamp() {
    this.start = System.currentTimeMillis();
    this.stage = Stage.RUNNING;
  }

  public void setStartTimestamp(long start) {
    this.start = start;
  }

  public void setStopTimestamp() {
    this.stage = Stage.FINISHED;
    this.stop = System.currentTimeMillis();
  }

  public void setStopTimestamp(long stop) {
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
}
