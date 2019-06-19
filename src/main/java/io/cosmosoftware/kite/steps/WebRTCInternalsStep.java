package io.cosmosoftware.kite.steps;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.pages.WebRTCInternalPage;
import io.cosmosoftware.kite.report.Reporter;

public class WebRTCInternalsStep extends TestStep {

  private final WebRTCInternalPage webRTCInternalPage;

  public WebRTCInternalsStep(Runner params) {
    super(params);
    setStepPhase(StepPhase.ALL);
    webRTCInternalPage = new WebRTCInternalPage(params);
  }

  @Override
  protected void step() throws KiteTestException {
    Reporter.getInstance().textAttachment(report, "webrtc-internals dump",
      webRTCInternalPage.downloadDump(), "plain");
  }

  @Override
  public String stepDescription() {
    return "Gets the webrtc-internals dump.";
  }



}
