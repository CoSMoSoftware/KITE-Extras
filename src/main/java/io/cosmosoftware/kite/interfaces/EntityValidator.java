/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.interfaces;

import io.cosmosoftware.kite.exception.BadEntityException;

/**
 * The Interface EntityValidator.
 */
public interface EntityValidator {

  /**
   * Validate.
   *
   * @throws BadEntityException the bad entity exception
   */
  public void validate() throws BadEntityException;

}
