package io.cosmosoftware.kite.steps;

public enum StepPhase {
  DEFAULT(""),
  RAMPUP("Ramp Up "),
  LOADREACHED("Load Reached "),
  ALL("All");

  private final String name;

  private StepPhase(String value) {
    name = value;
  }
  
  public String getName() {
    return this.name;
  }

  public String getShortName() {
    switch (this) {
      case RAMPUP:
        return "RU ";
      case LOADREACHED:
        return "LR ";
      default:
        return "";
    }
  }
  
}
