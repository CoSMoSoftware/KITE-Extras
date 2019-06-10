package io.cosmosoftware.kite.report;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import java.util.LinkedHashMap;

public class ParamList extends LinkedHashMap<String, String> {
  
  public JsonArray toJson() {
    JsonArrayBuilder builder = Json.createArrayBuilder();
    for (String key : this.keySet()) {
      builder.add(Json.createObjectBuilder()
        .add("name", key)
        .add("value", this.get(key)).build());
    }
    return builder.build();
  }
  
  @Override
  public String toString() {
    return toJson().toString();
  }
  
}
