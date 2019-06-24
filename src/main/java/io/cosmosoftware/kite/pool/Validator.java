/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.pool;

/**
 * Represents the functionality to validate an object of the pool and to subsequently perform
 * cleanup activities.
 *
 * @param <T> the type parameter
 * @author Swaranga
 */
public interface Validator<T> {

  /**
   * Performs any cleanup activities before discarding the object. For example before discarding
   * database connection objects, the pool will want to close the connections. This is done via the
   * <code>invalidate()</code> method.
   *
   * @param t the object to cleanup
   */

  void invalidate(T t);

  /**
   * Checks whether the object is valid.
   *
   * @param t the object to check.
   * @return <code>true</code> if the object is valid else <code>false</code>.
   */
  boolean isValid(T t);
}
