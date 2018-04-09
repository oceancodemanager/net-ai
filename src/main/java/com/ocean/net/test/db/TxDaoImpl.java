package com.ocean.net.test.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TxDaoImpl {

	private static final Logger logger = LoggerFactory.getLogger(TxDaoImpl.class);
	@Autowired
	protected HibernateTemplate hibernateTemplate;

	void addEntity() {
		TxEntity entity = new TxEntity();
		entity.setId(2);
		entity.setName("sss");
		hibernateTemplate.save(entity);
		logger.debug("save over:" + entity);
	}
	// JAVA8 LUMDA表达式
	// return hibernateTemplateMysql.execute((Session session)-> {
	// String hql = "from User where id=?";
	// Query query = session.createQuery(hql);
	// query.setParameter(0, id);
	// return query.uniqueResult();
}
