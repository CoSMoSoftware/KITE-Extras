/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.steps;

import io.cosmosoftware.kite.interfaces.Runner;

import static io.cosmosoftware.kite.entities.Timeouts.DEFAULT_TIMEOUT;
import static io.cosmosoftware.kite.entities.Timeouts.ONE_SECOND_INTERVAL;

public abstract class TestCheck extends TestStep {
  protected int checkTimeout = DEFAULT_TIMEOUT;
  protected int checkInterval = ONE_SECOND_INTERVAL;
  
  public TestCheck(Runner runner) {
    super(runner);
  }
  
  public void setCheckInterval(int checkInterval) {
    this.checkInterval = checkInterval;
  }
  
  public void setCheckTimeout(int checkTimeout) {
    this.checkTimeout = checkTimeout;
  }
  
  public int getCheckInterval() {
    return checkInterval;
  }
  
  public int getCheckTimeout() {
    return checkTimeout;
  }
}
