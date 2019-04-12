/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.pool;

/**
 * The type Time elasped exception.
 */
public class TimeElapsedException extends Exception {
  
  /**
   * Instantiates a new Time elasped exception.
   */
  public TimeElapsedException() {
    super("The specified waiting time has been elasped");
  }
  
  /**
   * Instantiates a new Time elasped exception.
   *
   * @param e the e
   */
  public TimeElapsedException(InterruptedException e) {
    super(e);
  }
  
}
