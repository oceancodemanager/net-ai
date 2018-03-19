package com.ocean.net.test.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TxDaoImpl {
	@Autowired
	protected HibernateTemplate hibernateTemplate;

	void addEntity() {
		TxEntity entity = new TxEntity();
		entity.setId(2);
		entity.setName("sss");
		hibernateTemplate.save(entity);
	}
	// JAVA8 LUMDA表达式
	// return hibernateTemplateMysql.execute((Session session)-> {
	// String hql = "from User where id=?";
	// Query query = session.createQuery(hql);
	// query.setParameter(0, id);
	// return query.uniqueResult();
}
