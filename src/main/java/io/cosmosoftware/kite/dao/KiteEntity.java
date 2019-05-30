/*
 * Copyright 2018 Cosmo Software
 */

package io.cosmosoftware.kite.dao;

import java.io.Serializable;

import org.json.JSONObject;

import io.cosmosoftware.kite.interfaces.EntityValidator;
import io.cosmosoftware.kite.exception.BadEntityException;

/**
 * The Class KiteEntity.
 */
public abstract class KiteEntity implements EntityValidator, Serializable, Cloneable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.cosmosoftware.kite.interfaces.EntityValidator#validate()
	 */
	@Override
	public void validate() throws BadEntityException {
		// Do Nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new JSONObject(this).toString();
	}

	
}
