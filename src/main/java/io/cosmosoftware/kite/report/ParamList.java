package io.cosmosoftware.kite.report;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.util.ArrayList;

public class ParamList extends ArrayList<ParamList.Parameter> {
  
  public JsonArray toJson() {
    JsonArrayBuilder builder = Json.createArrayBuilder();
    for (Parameter label : this) {
      builder.add(label.toJson());
    }
    return builder.build();
  }
  
  public synchronized void addLabel(String name, String value) {
    this.add(new Parameter(name,value));
  }
  
  @Override
  public String toString() {
    return toJson().toString();
  }
  
  public class Parameter {
    private String name;
    private String value;
  
    public Parameter(String name, String value) {
      if (name != null && value!= null) {
        this.name = name;
        this.value = value;
      }
    }
  
    public JsonObject toJson() {
      return Json.createObjectBuilder()
        .add("name", name)
        .add("value", value).build();
    }
  }
  
}
