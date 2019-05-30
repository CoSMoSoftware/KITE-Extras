package io.cosmosoftware.kite.report;

import io.cosmosoftware.kite.entities.Stage;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.UUID;

public class Entity {
  protected String name;
  protected String uuid;
  protected long start;
  protected long stop;
  protected String stage;
  
  
  protected Entity(String name) {
    this.name = name;
    this.stage = Stage.SCHEDULED;
    this.uuid = UUID.randomUUID().toString();
  }
  
  public void setName(String name) {
    this.name = name;
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
  
  protected JsonObjectBuilder getJsonBuilder() {
    return Json.createObjectBuilder()
      .add("name", this.name)
      .add("start", this.start)
      .add("stop", this.stop)
      ;
  }
  
  public JsonObject toJson() {
    return this.getJsonBuilder().build();
  }
  
  @Override
  public String toString() {
    return toJson().toString();
  }
}
