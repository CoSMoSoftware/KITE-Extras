package io.cosmosoftware.kite.steps;

import org.openqa.selenium.WebDriver;

import static io.cosmosoftware.kite.entities.Timeouts.DEFAULT_TIMEOUT;
import static io.cosmosoftware.kite.entities.Timeouts.ONE_SECOND_INTERVAL;

public abstract class TestCheck extends TestStep {
  protected int checkTimeout = DEFAULT_TIMEOUT;
  protected int checkInterval =ONE_SECOND_INTERVAL;
  
  public TestCheck(WebDriver webDriver) {
    super(webDriver);
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
