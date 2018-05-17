package com.acloudchina.coap.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.acloudchina.bee.rpc.PushToClientService;

@org.springframework.web.bind.annotation.RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class RestController {
	@Autowired
	private PushToClientService pushToClientService;

	@RequestMapping(value = "/push", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String> push(String tenantId, String endpointId) {
		pushToClientService.pushCmdToClient(tenantId, endpointId, "Test:" + endpointId);
		Map<String, String> m = new HashMap<String, String>();
		m.put("result", "ok");
		return m;
	}
}
