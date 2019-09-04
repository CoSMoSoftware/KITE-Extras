/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.steps;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

public class ResizeWindowStep extends TestStep {

  protected final int width;
  protected final int height;
  protected final int x;
  protected final int y;

  public ResizeWindowStep(Runner runner, int width, int height) {
    super(runner);
    this.width = width;
    this.height = height;
    this.x = -1;
    this.y = -1;
  }

  public ResizeWindowStep(Runner runner, int width, int height, int x, int y) {
    super(runner);
    this.width = width;
    this.height = height;
    this.x = x;
    this.y = y;
  }

  @Override
  public String stepDescription() {
    return "Setting the window size to " + width + "x" + height;
  }

  @Override
  protected void step() throws KiteTestException {
    Dimension currentSize = webDriver.manage().window().getSize();
    Dimension d = new Dimension(width, height);
    webDriver.manage().window().setSize(d);
    if (x != -1 && y != -1) {
      Point p = new Point(Math.min(x, currentSize.width - width), Math.min(y, currentSize.height - height));
      webDriver.manage().window().setPosition(p);
    }
  }
}
