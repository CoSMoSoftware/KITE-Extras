/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.usrmgmt;

import org.json.JSONObject;

/**
 * The type Rc entity.
 */
public class Entity implements Cloneable {
  
  @Override
  public String toString() {
    return new JSONObject(this).toString();
  }
  
}
