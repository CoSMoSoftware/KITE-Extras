/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.entities;

/**
 * The Enum OsType.
 */
public enum OsType {
  /**
   * The mac.
   */
  MAC,
  /**
   * The windows10.
   */
  WINDOWS10,
  /**
   * The windows8.
   */
  WINDOWS8,
  /**
   * The windows7.
   */
  WINDOWS7,
  /**
   * The debian.
   */
  DEBIAN,
  /**
   * The ubuntu.
   */
  UBUNTU,
  /**
   * The kali.
   */
  KALI,
  /**
   * The centos.
   */
  CENTOS,
  /**
   * The android.
   */
  ANDROID;

  public boolean isUbuntu() { return this.equals(UBUNTU) || this.equals(DEBIAN) || this.equals(CENTOS); }
  public boolean isWindows() { return this.equals(WINDOWS7) || this.equals(WINDOWS8) || this.equals(WINDOWS10); }
}
