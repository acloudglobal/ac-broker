package com.acloudchina.coap.security;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acloudchina.auth.AuthBean;
import com.acloudchina.auth.AuthHandle;
import com.acloudchina.auth.ValidationInfo;
import com.acloudchina.event.common.JsonUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService {

	@Autowired
	private AuthHandle authHandle;

	public boolean validate(Map<String, String> mp) {
		log.info("validate Map:"+mp);
		if (mp == null) {
			return validate();
		} else {
			AuthBean bean = new AuthBean();
			bean.setAuthType(mp.get("authType")==null?-1:Integer.parseInt(mp.get("authType")));
			bean.setEndpointId(mp.get("endpointId"));
			bean.setPassword(mp.get("password"));
			bean.setTenantId(mp.get("tenant"));
			bean.setToken(mp.get("token"));
			bean.setUsername(mp.get("username"));
		
			ValidationInfo auth = authHandle.auth(bean);
			if (auth != null && auth.getAuth_valid()) {
				return true;
			}
		}

		return false;
	}

	public boolean validate() {
		return false;
	}
}
