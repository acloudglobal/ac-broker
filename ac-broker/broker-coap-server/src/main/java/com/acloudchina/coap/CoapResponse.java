package com.acloudchina.coap;

import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class CoapResponse {

	public static void createAndSendResponse(ResponseCode code, String message, CoapExchange exchange) {
		Response response = new Response(code);
		response.setPayload(message);
		exchange.respond(response);
	}

	public static void createAndSendResponse(ResponseCode code, byte[] message, CoapExchange exchange) {
		Response response = new Response(code);
		response.setPayload(message);
		exchange.respond(response);
	}
}
