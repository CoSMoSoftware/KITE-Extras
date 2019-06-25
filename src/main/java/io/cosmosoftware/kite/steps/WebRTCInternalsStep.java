package io.cosmosoftware.kite.steps;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.pages.WebRTCInternalPage;
import io.cosmosoftware.kite.report.Reporter;

public class WebRTCInternalsStep extends TestStep {

  private final WebRTCInternalPage webRTCInternalPage;

  public WebRTCInternalsStep(Runner runner) {
    super(runner);
    setStepPhase(StepPhase.ALL);
    webRTCInternalPage = new WebRTCInternalPage(runner);
  }

  @Override
  protected void step() throws KiteTestException {
    reporter.textAttachment(report, "webrtc-internals dump",
        webRTCInternalPage.downloadDump(), "plain");
  }

  @Override
  public String stepDescription() {
    return "Gets the webrtc-internals dump.";
  }


}
