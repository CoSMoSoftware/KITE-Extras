/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.entities;

/**
 * This class contains the common values used in various tests and classes.
 */
public class Timeouts {
  
  /**
   * The constant HALF_SECOND_INTERVAL.
   */
  public static final int HALF_SECOND_INTERVAL = 500;
  /**
   * The constant ONE_SECOND_INTERVAL.
   */
  public static final int ONE_SECOND_INTERVAL = HALF_SECOND_INTERVAL * 2;
  /**
   * The constant THREE_SECOND_INTERVAL.
   */
  public static final int THREE_SECOND_INTERVAL = ONE_SECOND_INTERVAL * 3;
  /**
   * The constant FIVE_SECOND_INTERVAL.
   */
  public static final int FIVE_SECOND_INTERVAL = ONE_SECOND_INTERVAL * 5;
  /**
   * The constant TEN_SECOND_INTERVAL.
   */
  public static final int TEN_SECOND_INTERVAL = ONE_SECOND_INTERVAL * 10;
  /**
   * The constant DEFAULT_GET_STAT_DURATION.
   */
  public static final int DEFAULT_GET_STAT_DURATION = TEN_SECOND_INTERVAL;
  /**
   * The constant SHORT_TIMEOUT.
   */
  public static final int SHORT_TIMEOUT = ONE_SECOND_INTERVAL * 30;
  /**
   * The constant DEFAULT_TIMEOUT.
   */
  public static final int DEFAULT_TIMEOUT = ONE_SECOND_INTERVAL * 60;
  /**
   * The constant EXTENDED_TIMEOUT.
   */
  public static final int EXTENDED_TIMEOUT = DEFAULT_TIMEOUT * 5;
  
  /**
   * The constant TEN_SECOND_INTERVAL_IN_SECONDS.
   */
  public static final int TEN_SECOND_INTERVAL_IN_SECONDS = 10;
  /**
   * The constant SHORT_TIMEOUT_IN_SECONDS.
   */
  public static final int SHORT_TIMEOUT_IN_SECONDS = 30;
  /**
   * The constant DEFAULT_TIMEOUT_IN_SECONDS.
   */
  public static final int DEFAULT_TIMEOUT_IN_SECONDS = 60;
  /**
   * The constant EXTENDED_TIMEOUT_IN_SECONDS.
   */
  public static final int EXTENDED_TIMEOUT_IN_SECONDS = DEFAULT_TIMEOUT_IN_SECONDS * 5;
  
}
