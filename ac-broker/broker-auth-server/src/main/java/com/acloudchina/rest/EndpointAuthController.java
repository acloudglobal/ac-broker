package com.acloudchina.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.acloudchina.auth.AuthBean;
import com.acloudchina.auth.AuthHandle;
import com.acloudchina.auth.ValidationInfo;

@org.springframework.web.bind.annotation.RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class EndpointAuthController {

	@Autowired
	private AuthHandle jdbcHandle;

	@RequestMapping(value = "/endpoint/auth", method = RequestMethod.POST)
	@ResponseBody
	public ValidationInfo auth(@RequestBody AuthBean auth) {
		ValidationInfo info = jdbcHandle.auth(auth, auth.getAuthType());
		return info;
	}
}
