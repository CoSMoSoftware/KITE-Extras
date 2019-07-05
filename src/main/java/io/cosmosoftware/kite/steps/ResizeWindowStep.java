package io.cosmosoftware.kite.steps;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import org.openqa.selenium.Dimension;

public class ResizeWindowStep extends TestStep {
  
  protected final int width;
  protected final int height;

  public ResizeWindowStep(Runner runner, int width, int height) {
    super(runner);
    this.width = width;
    this.height = height;
  }

  @Override
  public String stepDescription() {
    return "Setting the window size to " + width + "x" + height;
  }

  @Override
  protected void step() throws KiteTestException {
    Dimension d = new Dimension(width,height);
    webDriver.manage().window().setSize(d);
  }
}
