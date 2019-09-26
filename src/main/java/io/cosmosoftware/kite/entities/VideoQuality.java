package io.cosmosoftware.kite.entities;

public enum VideoQuality {
  VIDEO ("video"),
  JERKY ("jerky"),
  FREEZE ("freeze"),
  BLANK ("blank");

  final private String value;
  VideoQuality(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
