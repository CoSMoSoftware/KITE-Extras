/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.steps;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;

import static io.cosmosoftware.kite.entities.Timeouts.ONE_SECOND_INTERVAL;
import static io.cosmosoftware.kite.util.TestUtils.waitAround;
import static io.cosmosoftware.kite.util.WebDriverUtils.poke;

public class StayInMeetingStep extends TestStep {

  private final int meetingDuration;

  public StayInMeetingStep(Runner runner, int meetingDuration) {
    super(runner);
    this.meetingDuration = meetingDuration;
    setStepPhase(StepPhase.ALL);
  }

  @Override
  protected void step() {
    logger.info(stepDescription());
    for (int waitTime = 0; waitTime < meetingDuration; waitTime++) {
      waitAround(ONE_SECOND_INTERVAL);
      poke(this.webDriver);
    }
  }

  @Override
  public String stepDescription() {
    return "Stay in the meeting for " + meetingDuration + "s.";
  }
}
