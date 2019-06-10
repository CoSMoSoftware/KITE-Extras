/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.dao;

import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.TransactionManager;
import java.util.List;

/**
 * The Class DaoManager.
 *
 * @param <T> the generic type
 */
public class DaoManager<T> {

	private static final Logger logger = Logger.getLogger(DaoManager.class.getName());
	private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("KITEServer");
	private final EntityManager em = emf.createEntityManager();

	/**
	 * Instantiates a new dao manager.
	 */
	public DaoManager() {

	}

	/**
	 * Insert.
	 *
	 * @param entity the entity
	 * @throws Exception if the entity is not implemented correctly
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
	 * @param entity the entity
	 * @return the generic type object
	 * @throws Exception if the entity is not implemented correctly
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
	 * @param primaryKey the primary key
	 * @param entityClass the entity class
	 * @return the t
	 * @throws Exception if the entity is not implemented correctly

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
	 * @throws Exception if the entity is not implemented correctly

	 */
	public List<T> select(String query, Class<T> cl) throws Exception {
		return em.createQuery(query, cl).getResultList();
	}

	/**
	 * Gets the single result.
	 *
	 * @param query the query
	 * @param cl the class
	 * @return the last id
	 * @throws Exception if the entity is not implemented correctly
	 */
	public Object getSingleResult(String query, Class cl) throws Exception {
		return em.createQuery(query, cl).setMaxResults(1).getSingleResult();
	}

	/**
	 * Delete.
	 *
	 * @param entity the entity
	 * @throws Exception if the entity is not implemented correctly
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
	 * @param tm the tm
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
