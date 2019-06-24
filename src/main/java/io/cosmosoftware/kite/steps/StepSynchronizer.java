package io.cosmosoftware.kite.steps;

import io.cosmosoftware.kite.exception.KiteTestException;

public interface StepSynchronizer {

  boolean stepCompleted(String stepName) throws KiteTestException;
}
