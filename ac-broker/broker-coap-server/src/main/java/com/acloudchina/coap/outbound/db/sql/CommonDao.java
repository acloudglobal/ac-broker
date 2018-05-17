package com.acloudchina.coap.outbound.db.sql;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class CommonDao {

	@Autowired
	@PersistenceContext
	EntityManager entityManager;


	/**
	 */
	public List<Object[]> getCommands() {
		String sql = "select * from *****";
		Query query = entityManager.createNativeQuery(sql);
		List<Object[]> list = query.getResultList();
		return list;
	}
	
}
