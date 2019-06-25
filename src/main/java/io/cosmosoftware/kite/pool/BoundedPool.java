/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.pool;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * The type Bounded pool.
 *
 * @param <T> the type parameter
 */
public class BoundedPool<T> extends AbstractPool<T> {

  final private ObjectFactory<T> objectFactory;
  final private int size;
  final private Pool.Validator<T> validator;
  private Queue<T> objects;
  private Semaphore permits = new Semaphore(0);

  private volatile boolean shutdownCalled;

  /**
   * Instantiates a new Bounded pool.
   *
   * @param size the size
   * @param validator the validator
   * @param objectFactory the object factory
   */
  public BoundedPool(int size, Pool.Validator<T> validator, ObjectFactory<T> objectFactory) {
    super();

    this.objectFactory = objectFactory;
    this.size = size;
    this.validator = validator;

    objects = new LinkedBlockingQueue<T>();

    initializeObjects();

    shutdownCalled = false;
  }

  private void clearResources() {
    for (T t : objects) {
      validator.invalidate(t);
    }
  }

  @Override
  public T get() {
    T t = null;

    if (!shutdownCalled) {
      if (permits.tryAcquire()) {
        t = objects.poll();
      }
    } else {
      throw new IllegalStateException("Object pool already shutdown");
    }

    return t;
  }

  @Override
  protected void handleInvalidReturn(T t) {

  }

  private void initializeObjects() {
    for (int i = 0; i < size; i++) {
      objects.add(objectFactory.createNew());
    }
  }

  @Override
  protected boolean isValid(T t) {
    return validator.isValid(t);
  }

  @Override
  protected void returnToPool(T t) {
    boolean added = objects.add(t);

    if (added) {
      permits.release();
    }
  }

  @Override
  public void shutdown() {
    shutdownCalled = true;

    clearResources();
  }
}
