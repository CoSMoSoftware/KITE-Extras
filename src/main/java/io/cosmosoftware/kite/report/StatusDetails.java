/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.report;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class StatusDetails {

  private boolean known = false;
  private boolean muted = false;
  private boolean flaky = false;
  private String message;
  private String trace;
  private int code = 0; // 0,1,2,3 for PASSED (DEFAULT), FAILED, BROKEN and SKIPPED

  public StatusDetails() {
  }

  public void setFlaky(boolean flaky) {
    this.flaky = flaky;
  }

  public void setKnown(boolean known) {
    this.known = known;
  }

  public void setMuted(boolean mute) {
    this.muted = mute;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setTrace(String trace) {
    this.trace = trace;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }

  private JsonObjectBuilder getJsonBuilder() {
    JsonObjectBuilder builder = Json.createObjectBuilder()
        .add("known", known)
        .add("muted", muted)
        .add("flaky", flaky);
    if (message != null) {
      builder.add("message", message);
    }
    if (trace != null) {
      builder.add("trace", trace);
    }
    return builder;
  }

  public String getMessage() {
    return message;
  }

  public JsonObject toJson() {
    return getJsonBuilder().build();
  }
}
