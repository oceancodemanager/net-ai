package com.ocean.net.test.db;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring-persistence.xml", "classpath*:/spring-beans.xml" })
public class TxTest extends AbstractJUnit4SpringContextTests {
	@Autowired
	TxDaoImpl txDaoImpl;

	@Test
	public void testTx() {
		txDaoImpl.addEntity();
	}
}
