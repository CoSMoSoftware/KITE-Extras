package io.cosmosoftware.kite.steps;

import static io.cosmosoftware.kite.entities.Timeouts.ONE_SECOND_INTERVAL;
import static io.cosmosoftware.kite.entities.Timeouts.SHORT_TIMEOUT_IN_SECONDS;
import static io.cosmosoftware.kite.util.TestUtils.waitAround;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.report.Reporter;
import io.cosmosoftware.kite.report.Status;

/**
 * Special Step implementation that waits for a given step to complete on all other runners.
 * <p>
 * Usage: In your KiteBaseTest implementation, add this step as follows to wait for the previous
 * step to complete: runner.addStep(new WaitForOthersStep(webDriver, this, runner.getLastStep()));
 */
public class WaitForOthersStep extends TestStep {

  private final TestStep stepToWaitFor;
  private final StepSynchronizer stepSynchronizer;
  private int timeoutInSeconds = SHORT_TIMEOUT_IN_SECONDS;

  /**
   * Instantiates a new Wait for others step.
   *
   * @param runner the Runner
   * @param stepSynchronizer the StepSynchronizer (implemented by KiteBaseTest)
   * @param stepToWaitFor the step to wait for
   */
  public WaitForOthersStep(Runner runner, StepSynchronizer stepSynchronizer,
      TestStep stepToWaitFor) {
    super(runner);
    this.stepSynchronizer = stepSynchronizer;
    this.stepToWaitFor = stepToWaitFor;
    setStepPhase(StepPhase.ALL);
  }

  /**
   * Sets timeout.
   *
   * @param timeout the timeout
   */
  public void setTimeout(int timeout) {
    this.timeoutInSeconds = timeout;
  }

  @Override
  protected void step() throws KiteTestException {
    int i = 0;
    while (!stepSynchronizer.stepCompleted(stepToWaitFor.getName())) {
      logger.debug("WaitForOtherRunners waiting for " + stepToWaitFor.getName());
      i++;
      waitAround(ONE_SECOND_INTERVAL);
      if (i > timeoutInSeconds) {
        throw new KiteTestException(
            "Timed out waiting for other runners to complete the step " + stepToWaitFor.getName(),
            Status.FAILED);
      }
    }
    reporter.textAttachment(report, "All runners completed",
        "" + stepToWaitFor.getName() + " within " + i + "s.", "plain");
  }

  @Override
  public String stepDescription() {
    return "Waiting for " + stepToWaitFor.getName() + " to complete.";
  }

}


