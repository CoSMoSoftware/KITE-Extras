/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

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
  
  public boolean shouldProcess(TestStep step) {
    StepPhase stepPhase = step.getStepPhase();
    switch(stepPhase) {
      case DEFAULT:
        return this == RAMPUP || stepPhase == this;
      case RAMPUP:
      case LOADREACHED:
        return stepPhase == this;
      case ALL:
        return true;
    }
    //should never reach here
    return false;
  }

  public boolean isLastPhase() {
    return this != RAMPUP;
  }
  
}
