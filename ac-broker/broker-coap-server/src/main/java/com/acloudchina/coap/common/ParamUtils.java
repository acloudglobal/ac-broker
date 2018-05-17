package com.acloudchina.coap.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParamUtils {

	public static Map<String, String> transelateParam(List<String> paramList, List<String> paths) {
		Map<String, String> jo = new HashMap<>();
		for (String params : paramList) {
			String[] kv = params.split("=");
			jo.put(kv[0], kv[1]);
		}

		if (jo.get("tenant") == null) {
			String tenantId = paths.get(UriPositionDefine.TENANT_POSITION);
			jo.put("tenant", tenantId);
		}

		if (jo.get("endpointId") == null) {
			String endpointId = paths.get(UriPositionDefine.ENDPOINT_POSITION);
			jo.put("endpointId", endpointId);
		}
		return jo;
	}

}
