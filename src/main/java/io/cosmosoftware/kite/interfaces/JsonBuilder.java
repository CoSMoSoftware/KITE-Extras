/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.interfaces;

import javax.json.JsonObjectBuilder;

/**
 * The Interface JsonBuilder.
 */
public interface JsonBuilder {

  /**
   * Builds the json object builder.
   *
   * @return the json object builder
   */
  public JsonObjectBuilder buildJsonObjectBuilder() throws NullPointerException;
}
