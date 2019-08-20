/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.pool;

import java.util.concurrent.*;

/**
 * The type Bounded blocking pool.
 *
 * @param <T> the type parameter
 */
public final class BoundedBlockingPool<T> extends AbstractPool<T> implements BlockingPool<T> {

  final private ObjectFactory<T> objectFactory;
  final private int size;
  final private Validator<T> validator;
  private ExecutorService executor = Executors.newCachedThreadPool();
  private BlockingQueue<T> objects;
  private volatile boolean shutdownCalled;

  /**
   * Instantiates a new Bounded blocking pool.
   *
   * @param size the size
   * @param validator the validator
   * @param objectFactory the object factory
   */
  public BoundedBlockingPool(int size, Validator<T> validator, ObjectFactory<T> objectFactory) {
    super();

    this.objectFactory = objectFactory;
    this.size = size;
    this.validator = validator;

    objects = new LinkedBlockingQueue<T>(size);

    initializeObjects();

    shutdownCalled = false;
  }

  private void clearResources() {
    for (T t : objects) {
      validator.invalidate(t);
    }
  }

  public T get() {
    if (!shutdownCalled) {
      T t = null;

      try {
        t = objects.take();
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
      }

      return t;
    }

    throw new IllegalStateException("Object pool is already shutdown");
  }

  public T get(long timeOut, TimeUnit unit) throws TimeElapsedException {
    if (!shutdownCalled) {
      T t = null;

      InterruptedException e = null;
      try {
        t = objects.poll(timeOut, unit);
      } catch (InterruptedException ie) {
        e = ie;
        Thread.currentThread().interrupt();
      } finally {
        if (e != null) {
          throw new TimeElapsedException(e);
        }
        if (t == null) {
          throw new TimeElapsedException();
        }
        return t;
      }
    }

    throw new IllegalStateException("Object pool is already shutdown");
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
    if (validator.isValid(t)) {
      executor.submit(new ObjectReturner(objects, t));
    }
  }

  public void shutdown() {
    shutdownCalled = true;

    executor.shutdownNow();

    clearResources();
  }

  private class ObjectReturner<E> implements Callable<Void> {

    private E e;
    private BlockingQueue<E> queue;

    /**
     * Instantiates a new Object returner.
     *
     * @param queue the queue
     * @param e the e
     */
    public ObjectReturner(BlockingQueue<E> queue, E e) {
      this.queue = queue;
      this.e = e;
    }

    public Void call() {
      while (true) {
        try {
          queue.put(e);
          break;
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
        }
      }

      return null;
    }
  }
}
