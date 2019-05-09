package io.cosmosoftware.kite.exception;

import io.cosmosoftware.kite.report.Status;

public class KiteInteractionException extends KiteTestException {
  
  public KiteInteractionException(String message) {
    super(message, Status.BROKEN);
  }
  
  public KiteInteractionException(String message, Throwable cause) {
    super(message, Status.BROKEN, cause);
  }
}
