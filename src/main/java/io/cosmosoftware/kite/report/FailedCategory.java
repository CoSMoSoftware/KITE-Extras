package io.cosmosoftware.kite.report;

import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class FailedCategory extends Category {

  public FailedCategory(String name) {
    super(name);
    this.addStatus("failed");
  }
}
