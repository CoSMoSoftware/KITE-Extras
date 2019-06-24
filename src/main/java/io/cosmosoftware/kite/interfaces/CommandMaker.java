/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.interfaces;

/**
 * The Interface CommandMaker.
 */
public interface CommandMaker {

  /**
   * Make command.
   *
   * @param isHub the is hub
   * @return the string
   */
  public String makeCommand(boolean isHub, boolean isWindows);
}
