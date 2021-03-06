package com.acloudchina.auth.jdbc;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acloudchina.auth.AuthConstants;

import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@Slf4j
public class AuthDao {

	@Autowired
	@PersistenceContext
	EntityManager entityManager;
	
	
	public int getAuthType(String tenantId,String endpointId){
		return AuthConstants.AUTH_CLIENTID_USERNAME_PASSWORD;
	}

	/**
	 * clientId认证
	 */
	public boolean auth(String endpointId, String tenantId) {
		String sql = "select * from ac_endpoint_auth where tenant_id='" + tenantId + "' and endpoint_id='" + endpointId
				+ "' and deleted=0";
		
		log.info("auth SQL:"+sql);
		
		Query query = entityManager.createNativeQuery(sql);
		int resultCount = query.getResultList() == null ? 0 : query.getResultList().size();
		boolean result = resultCount > 0 ? true : false;
		return result;
	}

	/**
	 * clientId+username+password认证
	 */
	public boolean auth(String endpointId, String tenantId, String userName, String password) {
		String sql = "select * from ac_endpoint_auth where tenant_id='" + tenantId + "' and endpoint_id='" + endpointId
				+ "' and username='" + userName + "' and password='" + password + "' and deleted=0";
		
		log.info("auth SQL:"+sql);
		
		Query query = entityManager.createNativeQuery(sql);
		int resultCount = query.getResultList() == null ? 0 : query.getResultList().size();
		return resultCount > 0 ? true : false;
	}

	/**
	 * token认证
	 */
	public boolean authToken(String token) {
		return false;
	}

}
