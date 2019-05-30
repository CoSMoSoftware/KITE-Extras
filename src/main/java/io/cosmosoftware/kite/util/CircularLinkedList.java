/*
 * Copyright 2018 Cosmo Software
 */

package io.cosmosoftware.kite.util;

import java.util.Collection;
import java.util.LinkedList;

/**
 * The Class CircularLinkedList.
 *
 * @param <E>
 *            the element type
 */
public class CircularLinkedList<E> extends LinkedList<E> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The pointer. */
	private int pointer = 0;

	/**
	 * Instantiates a new circular linked list.
	 */
	public CircularLinkedList() {
		super();
	}

	/**
	 * Instantiates a new circular linked list.
	 *
	 * @param collection
	 *            the collection
	 */
	public CircularLinkedList(Collection<? extends E> collection) {
		super(collection);
	}

	/**
	 * Gets the.
	 *
	 * @return the e
	 */
	public E get() {
		try {
			return this.get(pointer++);
		} catch (IndexOutOfBoundsException e) {
			pointer = 0;
			return this.get();
		}
	}

}
