package com.acloudchina.auth;

import lombok.Data;

@Data
public class AuthBean {

	private String username;
	private String password;
	private String tenantId;
	private String endpointId;
	private String token;
	private int authType;
	
}
