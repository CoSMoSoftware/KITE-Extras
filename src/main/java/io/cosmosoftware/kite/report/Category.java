package io.cosmosoftware.kite.report;

import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class Category {
  private final String name;
  private List<String> matchedStatuses = new ArrayList<>();
  private String messageRegex;

  public Category (String name) {
    this.name = name;
  }

  public void addStatus(String status) {
    this.matchedStatuses.add(status);
  }

  public void setMessageRegex(String message) {
    this.messageRegex = ".*" + message + ".*";
  }

  public JsonObject toJson() {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    builder.add("name", this.name);
    if (matchedStatuses.size() > 0) {
      JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
      for (String status :matchedStatuses) {
        arrayBuilder.add(status);
      }
      builder.add("matchedStatuses", arrayBuilder);
    }
    if (this.messageRegex != null) {
      builder.add("messageRegex", this.messageRegex);
    }
    return builder.build();
  }

  @Override
  public String toString() {
    return toJson().toString();
  }
}
