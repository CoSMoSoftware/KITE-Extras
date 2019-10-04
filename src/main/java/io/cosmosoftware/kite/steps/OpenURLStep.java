/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.steps;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.report.Status;
import java.util.ArrayList;
import java.util.List;

public class OpenURLStep extends TestStep {

  private final String url;
  private final List<String> errorMessages = new ArrayList<>();



  public OpenURLStep(Runner runner, String url) {
    super(runner);
    this.url = url;

    errorMessages.add("Unable to connect");
    errorMessages.add("Secure Connection Failed");
    errorMessages.add("The connection has timed out");
    errorMessages.add("We’re having trouble finding that site");

    errorMessages.add("This site can’t be reached");
    errorMessages.add("webpage is not available");
    errorMessages.add("404. That’s an error.");
  }

  @Override
  public String stepDescription() {
    return "Opening: " + this.url;
  }

  @Override
  protected void step() throws KiteTestException {
    this.webDriver.get(this.url);
    String html = webDriver.getPageSource();
    for (String errorMessage : this.errorMessages) {
      if (html.contains(errorMessage)) {
        throw new KiteTestException("Error opening page: " + errorMessage, Status.FAILED);
      }
    }
  }
}
