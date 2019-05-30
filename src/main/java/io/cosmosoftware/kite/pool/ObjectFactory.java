/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.pool;

/**
 * Represents the mechanism to create
 * new objects to be used in an object pool.
 *
 * @param <T> the type parameter
 *
 * @author Swaranga
 */
public interface ObjectFactory<T> {
  /**
   * Returns a new instance of an object of type T.
   *
   * @return T an new instance of the object of type T
   */
  T createNew();
}
