/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package io.cosmosoftware.kite.dao;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import javax.persistence.Entity;
import javax.persistence.NoResultException;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

/**
 * The Class KiteIdGenerator.
 */
public class KiteIdGenerator implements IdentifierGenerator, Configurable {

	private final static Logger logger = Logger.getLogger(KiteIdGenerator.class.getName());
	private static final String ID_DELIMITER = "_";
	private static final String NUMBER_FORMAT = "%s" + KiteIdGenerator.ID_DELIMITER + "%09d";
	private static final Map<String, Long> lastIdMap = new Hashtable<String, Long>();
	private String prefix;
	private int increment = 1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.id.IdentifierGenerator#generate(org.hibernate.engine.spi.
	 * SharedSessionContractImplementor, java.lang.Object)
	 */
	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
		Long lastId = KiteIdGenerator.lastIdMap.get(this.prefix);
		if (lastId == null)
			lastId = 0L;

		String tableName = obj.getClass().getAnnotation(Entity.class).name();
		String query = String.format("select e from %s e order by e.id desc",
				// session.getEntityPersister(obj.getClass().getName(),
				// obj).getIdentifierPropertyName(),
				tableName);

		logger.trace("query->" + query);

		try {
			Object object = new DaoManager<String>().getSingleResult(query, obj.getClass());
			String id = (String) object.getClass().getMethod("getId", null).invoke(object, null);
			Long lastDbId = Long.parseLong(id.split(KiteIdGenerator.ID_DELIMITER)[1]);
			lastId = (lastId > lastDbId ? lastId : lastDbId) + 1;
		} catch (NoResultException e) {
			// No Record in Database
			logger.trace(tableName + " was empty!");
			lastId += 1;
		} catch (Exception e) {
			throw new HibernateException(e);
		}

		KiteIdGenerator.lastIdMap.put(this.prefix, lastId);
		return String.format(KiteIdGenerator.NUMBER_FORMAT, this.prefix, lastId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.id.Configurable#configure(org.hibernate.type.Type,
	 * java.util.Properties, org.hibernate.service.ServiceRegistry)
	 */
	@Override
	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
		String prefixParam = params.getProperty("prefix");
		if (prefixParam == null)
			throw new MappingException("prefix is not provided for " + type.getName());
		this.prefix = prefixParam;
		this.increment = Integer.parseInt(params.getProperty("increment", Integer.toString(this.increment)));
	}

}