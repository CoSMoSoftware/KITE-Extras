/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.entities;

/**
 * The enum Platform.
 */
public enum Platform {
  /**
   * Android platform.
   */
  ANDROID,
  /**
   * Ios platform.
   */
  IOS,
  /**
   * Browser platform.
   */
  BROWSER;

  /**
   * Gets platform.
   *
   * @param value the value
   * @return the platform
   */
  public static Platform getPlatform(String value) {
    String valueLowerCase = value.toLowerCase();
    if (valueLowerCase.contains("and")) {
      return ANDROID;
    }
    if (valueLowerCase.contains("ios")) {
      return IOS;
    }

    return BROWSER;
  }
}
