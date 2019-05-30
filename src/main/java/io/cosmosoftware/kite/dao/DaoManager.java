/*
 * Copyright 2018 Cosmo Software
 */

package io.cosmosoftware.kite.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.TransactionManager;

import org.apache.log4j.Logger;

/**
 * The Class DaoManager.
 *
 * @param <T>
 *            the generic type
 */
public class DaoManager<T> {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(DaoManager.class.getName());

	/** The emf. */
	private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("KITEServer");

	/** The em. */
	private final EntityManager em = emf.createEntityManager();

	/**
	 * Instantiates a new dao manager.
	 */
	public DaoManager() {

	}

	/**
	 * Insert.
	 *
	 * @param entity
	 *            the entity
	 * @throws Exception
	 *             the exception
	 */
	public void insert(T entity) throws Exception {
		TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
		try {
			tm.begin();
			em.persist(entity);
			em.flush();
			tm.commit();
		} catch (Exception e) {
			rollback(tm);
			throw e;
		}
	}

	/**
	 * Update.
	 *
	 * @param entity
	 *            the entity
	 * @return the t
	 * @throws Exception
	 *             the exception
	 */
	public T update(T entity) throws Exception {
		T managedEntity = null;

		TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
		try {
			tm.begin();
			managedEntity = em.merge(entity);
			em.flush();
			tm.commit();
		} catch (Exception e) {
			rollback(tm);
			throw e;
		}

		return managedEntity;
	}

	/**
	 * Select.
	 *
	 * @param entityClass
	 *            the entity class
	 * @param primaryKey
	 *            the primary key
	 * @return the t
	 * @throws Exception
	 *             the exception
	 */
	public T select(Class<T> entityClass, Object primaryKey) throws Exception {
		return em.find(entityClass, primaryKey);
	}

	/**
	 * Select.
	 *
	 * @param query
	 *            the query
	 * @param cl
	 *            the cl
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */
	public List<T> select(String query, Class<T> cl) throws Exception {
		return em.createQuery(query, cl).getResultList();
	}

	/**
	 * Gets the single result.
	 *
	 * @param query
	 *            the query
	 * @param cl
	 *            the cl
	 * @return the last id
	 * @throws Exception
	 *             the exception
	 */
	public Object getSingleResult(String query, Class cl) throws Exception {
		return em.createQuery(query, cl).setMaxResults(1).getSingleResult();
	}

	/**
	 * Delete.
	 *
	 * @param entity
	 *            the entity
	 * @throws Exception
	 *             the exception
	 */
	public void delete(T entity) throws Exception {
		TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
		try {
			tm.begin();
			em.remove(entity);
			em.flush();
			tm.commit();
		} catch (Exception e) {
			rollback(tm);
			throw e;
		}
	}

	/**
	 * Rollback.
	 *
	 * @param tm
	 *            the tm
	 */
	private void rollback(TransactionManager tm) {
		try {
			tm.rollback();
		} catch (Exception e) {
			logger.warn(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	public void finalize() {
		em.close();
		emf.close();
	}

}
