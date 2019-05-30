/*
 * Copyright 2018 Cosmo Software
 */

package io.cosmosoftware.kite.load;

import io.cosmosoftware.kite.dao.KiteEntity;
import io.cosmosoftware.kite.interfaces.JsonBuilder;
import io.cosmosoftware.kite.interfaces.SampleData;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: Load.
 */

@Entity(name = LoadConfig.TABLE_NAME)
public class LoadConfig extends KiteEntity implements JsonBuilder, SampleData {

	/** The Constant TABLE_NAME. */
	final static String TABLE_NAME = "loadconfigs";

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	private String id;

	/** The count. */
	private int count;

	/** The increment. */
	private int increment;

	/** The interval. */
	private int interval;

	/** The result. */
	private LoadResult result;

	/**
	 * Instantiates a new load config.
	 */
	public LoadConfig() {
		super();
	}

	/**
	 * Instantiates a new load config.
	 *
	 * @param jsonObject
	 *            the json object
	 */
	public LoadConfig(JsonObject jsonObject) {
		this();

		// Mandatory
		this.count = jsonObject.getInt("count");
		this.increment = jsonObject.getInt("increment");
		this.interval = jsonObject.getInt("interval");
	}

	/**
	 * Instantiates a new load config.
	 *
	 * @param loadConfig
	 *            the load config
	 */
	public LoadConfig(LoadConfig loadConfig) {
		this();
		if (loadConfig != null) {
			this.count = loadConfig.count;
			this.increment = loadConfig.increment;
			this.interval = loadConfig.interval;
		}
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	@Id
	// @GeneratedValue(generator = "uuid")
	// @GenericGenerator(name = "uuid", strategy = "uuid2")
	@GeneratedValue(generator = LoadConfig.TABLE_NAME)
	@GenericGenerator(name = LoadConfig.TABLE_NAME, strategy = "io.cosmosoftware.kite.dao.KiteIdGenerator", parameters = {
			@Parameter(name = "prefix", value = "LOAD") })
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
	 * Gets the count.
	 *
	 * @return the count
	 */
	public int getCount() {
		return this.count;
	}

	/**
	 * Sets the count.
	 *
	 * @param count
	 *            the new count
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * Gets the increment.
	 *
	 * @return the increment
	 */
	public int getIncrement() {
		return this.increment;
	}

	/**
	 * Sets the increment.
	 *
	 * @param increment
	 *            the new increment
	 */
	public void setIncrement(int increment) {
		this.increment = increment;
	}

	/**
	 * Gets the interval.
	 *
	 * @return the interval
	 */
	public int getInterval() {
		return this.interval;
	}

	/**
	 * Sets the interval.
	 *
	 * @param interval
	 *            the new interval
	 */
	public void setInterval(int interval) {
		this.interval = interval;
	}

	/**
	 * Gets the result.
	 *
	 * @return the result
	 */
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	public LoadResult getResult() {
		return this.result;
	}

	/**
	 * Sets the result.
	 *
	 * @param result
	 *            the new result
	 */
	public void setResult(LoadResult result) {
		this.result = result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.cosmosoftware.kite.dao.JsonBuilder#buildJsonObjectBuilder()
	 */
	@Override
	public JsonObjectBuilder buildJsonObjectBuilder() {
		JsonObjectBuilder builder = Json.createObjectBuilder().add("count", this.count).add("increment", this.increment)
				.add("interval", this.interval);
		if (this.result != null)
			builder.add("result", this.result.buildJsonObjectBuilder());
		return builder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.cosmosoftware.kite.dao.SampleData#makeSampleData()
	 */
	@Override
	public SampleData makeSampleData() {
		this.count = 10;
		this.increment = 2;
		this.interval = 2;

		return this;
	}

}
