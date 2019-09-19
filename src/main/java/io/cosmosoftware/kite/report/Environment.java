package io.cosmosoftware.kite.report;

import java.util.HashMap;

public class Environment extends HashMap<String, String> {

  @Override
  public String toString() {
    String res = "";
    for (String key : this.keySet()) {
      res += key + "=" + this.get(key) + "\n";
    }
    return res;
  }
}
