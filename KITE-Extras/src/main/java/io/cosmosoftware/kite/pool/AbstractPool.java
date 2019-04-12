/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.pool;

/**
 * Represents an abstract pool, that defines the procedure
 * of returning an object to the pool.
 *
 * @param <T> the type parameter
 *
 * @author Swaranga
 */
abstract class AbstractPool<T> implements Pool<T> {
  /**
   * Handle invalid return.
   *
   * @param t the t
   */
  protected abstract void handleInvalidReturn(T t);
  
  /**
   * Is valid boolean.
   *
   * @param t the t
   *
   * @return the boolean
   */
  protected abstract boolean isValid(T t);
  
  /**
   * Returns the object to the pool.
   * The method first validates the object if it is
   * re-usable and then puts returns it to the pool.
   * <p>
   * If the object validation fails,
   * some implementations
   * will try to create a new one
   * and put it into the pool; however
   * this behaviour is subject to change
   * from implementation to implementation
   */
  @Override
  public final void release(T t) {
    if (isValid(t)) {
      returnToPool(t);
    } else {
      handleInvalidReturn(t);
    }
  }
  
  /**
   * Return to pool.
   *
   * @param t the t
   */
  protected abstract void returnToPool(T t);
}
