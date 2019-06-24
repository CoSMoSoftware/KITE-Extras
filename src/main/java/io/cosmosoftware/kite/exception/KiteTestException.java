/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.exception;

import io.cosmosoftware.kite.report.Status;

/**
 * The KiteTestException is thrown if there are a problem during the running of the test.
 */
public class KiteTestException extends Exception {

  private final Status status;
  private boolean continueOnFailure = false;
  private Object extra = null;

  /**
   * Constructs a KiteTestException with the specified detailed message, Status and cause.
   *
   * @param message the message
   * @param status the Allure Status
   * @param cause the Throwable cause
   */
  public KiteTestException(String message, Status status, Throwable cause) {
    super(message + "\r\n", cause);
    this.status = status;
  }

  /*  *//**
   * Constructs a KiteTestException with the specified detailed message and Status.
   *
   * @param message message
   * @param status  the Allure Status
   * @param extra   the extra
   *//*
  public KiteTestException(String message, Status status, Object extra) {
    super(message + "\r\n", null);
    this.status = status;
    this.extra = extra;
  }*/

  /**
   * Constructs a KiteTestException with the specified detailed message and Status.
   *
   * @param message message
   * @param status the Allure Status
   */
  public KiteTestException(String message, Status status) {
    super(message + "\r\n");
    this.status = status;
  }

  /**
   * Constructs a KiteTestException with the specified detailed message and Status.
   *
   * @param message message
   * @param status the Allure Status
   * @param cause the Throwable cause
   * @param continueOnFailure whether to continue with the test or not after this Exception.
   */
  public KiteTestException(String message, Status status, Throwable cause,
      boolean continueOnFailure) {
    super(message + "\r\n", cause);
    this.status = status;
    this.continueOnFailure = continueOnFailure;
  }

  /**
   * The extra information stored in an object for further processing.
   *
   * @return the extra object.
   */
  public Object getExtra() {
    return extra;
  }

  /**
   * Gets the status
   *
   * @return the status
   */
  public Status getStatus() {
    return status;
  }

  /**
   * Whether to continue with the test or not after this Exception.
   *
   * @return true if the test should after this Exception.
   */
  public boolean isContinueOnFailure() {
    return continueOnFailure;
  }
}
