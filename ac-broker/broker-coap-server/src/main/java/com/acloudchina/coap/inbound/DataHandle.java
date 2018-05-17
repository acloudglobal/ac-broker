package com.acloudchina.coap.inbound;

import java.util.Map;

import org.eclipse.californium.core.server.resources.CoapExchange;

public interface DataHandle {
	void handle(CoapExchange exchange, Map<String, String> param);
}
