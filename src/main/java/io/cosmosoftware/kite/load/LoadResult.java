/*
 * Copyright 2018 Cosmo Software
 */

package io.cosmosoftware.kite.load;

import io.cosmosoftware.kite.dao.KiteEntity;
import io.cosmosoftware.kite.interfaces.JsonBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * Entity implementation class for Entity: BrowserResult.
 */

@Entity(name = LoadResult.TABLE_NAME)
public class LoadResult extends KiteEntity implements JsonBuilder {

	/** The Constant TABLE_NAME. */
	final static String TABLE_NAME = "loadresults";

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	private String id;

	/** The given. */
	private int given;

	/** The achieved. */
	private int achieved;

	/** The user data. */
	private String userData;

	/** The persistent. */
	private boolean persistent = true;

	/**
	 * Instantiates a new load result.
	 */
	public LoadResult() {
		super();
	}

	/**
	 * Instantiates a new load result.
	 *
	 * @param persistent
	 *            the persistent
	 */
	public LoadResult(boolean persistent) {
		this();
		this.persistent = persistent;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	@Id
	// @GeneratedValue(generator = "uuid")
	// @GenericGenerator(name = "uuid", strategy = "uuid2")
	@GeneratedValue(generator = LoadResult.TABLE_NAME)
	@GenericGenerator(name = LoadResult.TABLE_NAME, strategy = "io.cosmosoftware.kite.dao.KiteIdGenerator", parameters = {
			@Parameter(name = "prefix", value = "LOAR") })
	public String getId() {
		return this.id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the given.
	 *
	 * @return the given
	 */
	public int getGiven() {
		return this.given;
	}

	/**
	 * Sets the given.
	 *
	 * @param given
	 *            the new given
	 */
	public void setGiven(int given) {
		this.given = given;
	}

	/**
	 * Gets the achieved.
	 *
	 * @return the achieved
	 */
	public int getAchieved() {
		return this.achieved;
	}

	/**
	 * Sets the achieved.
	 *
	 * @param achieved
	 *            the new achieved
	 */
	public void setAchieved(int achieved) {
		this.achieved = achieved;
	}

	/**
	 * Gets the user data.
	 *
	 * @return the user data
	 */
	public String getUserData() {
		return this.userData;
	}

	/**
	 * Sets the user data.
	 *
	 * @param userData
	 *            the new user data
	 */
	public void setUserData(String userData) {
		this.userData = userData;
	}

	/**
	 * Gets the persistent.
	 *
	 * @return the persistent
	 */
	@Transient
	public boolean getPersistent() {
		return this.persistent;
	}

	/**
	 * Sets the persistent.
	 *
	 * @param persistent
	 *            the new persistent
	 */
	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}

	/**
	 * Builds the json object builder.
	 *
	 * @return the json object builder
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.cosmosoftware.kite.dao.JsonBuilder#buildJsonObjectBuilder()
	 */
	@Override
	public JsonObjectBuilder buildJsonObjectBuilder() {
		JsonObjectBuilder builder = Json.createObjectBuilder().add("given", this.given).add("achieved", this.achieved);
		if (this.userData != null)
			builder.add("userData", this.userData);
		return builder;
	}

}
