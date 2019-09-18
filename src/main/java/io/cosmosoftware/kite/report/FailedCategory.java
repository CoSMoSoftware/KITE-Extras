package io.cosmosoftware.kite.report;

public class FailedCategory extends Category {

  public FailedCategory(String name) {
    super(name);
    this.addStatus("failed");
  }
}
