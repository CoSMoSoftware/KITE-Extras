package io.cosmosoftware.kite.steps;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;

import static io.cosmosoftware.kite.util.TestUtils.waitAround;

public class StayInMeetingStep extends TestStep {

  private final int meetingDuration;

  public StayInMeetingStep(Runner params, int meetingDuration) {
    super(params);
    this.meetingDuration = meetingDuration;
    setStepPhase(StepPhase.ALL);
  }
  
  @Override
  protected void step() throws KiteTestException {
    waitAround(meetingDuration * 1000);
  }
  
  @Override
  public String stepDescription() {
    return "Stay in the meeting for " + meetingDuration + "s.";
  }
}
