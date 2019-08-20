/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.steps;

import io.cosmosoftware.kite.interfaces.Runner;

import static io.cosmosoftware.kite.entities.Timeouts.DEFAULT_TIMEOUT;
import static io.cosmosoftware.kite.entities.Timeouts.ONE_SECOND_INTERVAL;

/**
 * The type Test check.
 */
public abstract class TestCheck extends TestStep {

  /**
   * The Check timeout.
   */
  protected int checkTimeout = DEFAULT_TIMEOUT;
  /**
   * The Check interval.
   */
  protected int checkInterval = ONE_SECOND_INTERVAL;

  /**
   * Instantiates a new Test check.
   *
   * @param runner the runner
   */
  public TestCheck(Runner runner) {
    super(runner);
  }

  /**
   * Gets check interval.
   *
   * @return the check interval
   */
  public int getCheckInterval() {
    return checkInterval;
  }

  /**
   * Sets check interval.
   *
   * @param checkInterval the check interval
   */
  public void setCheckInterval(int checkInterval) {
    this.checkInterval = checkInterval;
  }

  /**
   * Gets check timeout.
   *
   * @return the check timeout
   */
  public int getCheckTimeout() {
    return checkTimeout;
  }

  /**
   * Sets check timeout.
   *
   * @param checkTimeout the check timeout
   */
  public void setCheckTimeout(int checkTimeout) {
    this.checkTimeout = checkTimeout;
  }
}
