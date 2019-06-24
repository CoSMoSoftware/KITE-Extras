/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.report;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {

  FAILED("failed"),
  BROKEN("broken"),
  PASSED("passed"),
  SKIPPED("skipped");

  private final String value;

  Status(final String v) {
    value = v;
  }

  public static Status fromValue(final String v) {
    for (Status c : Status.values()) {
      if (c.value.equals(v)) {
        return c;
      }
    }
    throw new IllegalArgumentException(v);
  }

  @JsonValue
  public String value() {
    return value;
  }

}
