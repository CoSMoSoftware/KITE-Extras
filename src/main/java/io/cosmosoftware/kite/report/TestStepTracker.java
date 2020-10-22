package io.cosmosoftware.kite.report;

import javax.json.Json;
import javax.json.JsonObject;

public class TestStepTracker {
  private int total = 0;
  private int failure = 0;

  public void addFailure() {
    this.total ++;
    this.failure ++;
  }

  public void addSuccess() {
    this.total ++;
  }

  public int getFailure() {
    return failure;
  }

  public int getTotal() {
    return total;
  }

  public int getNumberOfSuccess() {
    return this.total - this.failure;
  }

  @Override
  public String toString() {
    return "FAILURE/TOTAL = " + this.failure + "/" + this.total;
  }

  public JsonObject toJson() {
    return Json.createObjectBuilder()
        .add("total", this.total)
        .add("failure", this.failure)
        .add("success", this.total - this.failure).build();
  }
}
