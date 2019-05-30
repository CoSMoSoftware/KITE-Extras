/*
 * Copyright 2019 Cosmo Software
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
	 * @throws BadEntityException
	 *             the bad entity exception
	 */
	public void validate() throws BadEntityException;

}
