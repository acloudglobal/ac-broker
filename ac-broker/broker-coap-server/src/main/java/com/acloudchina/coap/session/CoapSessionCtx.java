package com.acloudchina.coap.session;

import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.SessionException;

import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CoapSessionCtx {

	private SessionType sessionType;
	private final SessionId sessionId;
	private final CoapExchange exchange;
	private final String token;
	private final long timeout;
	private final AtomicInteger seqNumber = new AtomicInteger(2);

	public CoapSessionCtx(CoapExchange exchange, long timeout) {
		Request request = exchange.advanced().getRequest();
		this.token = request.getTokenString();
		this.sessionId = new CoapSessionId(request.getSource().getHostAddress(), request.getSourcePort(), this.token);
		this.exchange = exchange;
		this.timeout = timeout;
	}

	public void onNotificationMsg(String payload) throws Exception {
		Response response = new Response(ResponseCode.CONTENT);
		response.getOptions().setObserve(nextSeqNumber());
		response.setPayload(payload);
		
		exchange.respond(response);
	}
	
	public void onNotificationMsg(byte[] payload) throws Exception {
		Response response = new Response(ResponseCode.CONTENT);
		response.getOptions().setObserve(nextSeqNumber());
		response.setPayload(payload);
		exchange.respond(response);
	}

	public void onMsg(SessionCtrlMsg msg) throws SessionException {
		log.debug("[{}] onCtrl: {}", sessionId, msg);
		if (msg instanceof SessionCloseMsg) {
			onSessionClose((SessionCloseMsg) msg);
		}
	}

	private void onSessionClose(SessionCloseMsg msg) {
		if (msg.isTimeout()) {
			exchange.respond(ResponseCode.SERVICE_UNAVAILABLE);
		} else if (msg.isCredentialsRevoked()) {
			exchange.respond(ResponseCode.UNAUTHORIZED);
		} else {
			exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR);
		}
	}

	public SessionId getSessionId() {
		return sessionId;
	}
	
	public CoapExchange getExchange(){
		return exchange;
	}

	public String toString() {
		return "CoapSessionCtx [sessionId=" + sessionId + "]";
	}

	public boolean isClosed() {
		return exchange.advanced().isComplete() || exchange.advanced().isTimedOut();
	}

	public void close() {
		log.info("[{}] Closing processing context. Timeout: {}", sessionId, exchange.advanced().isTimedOut());
		if(exchange.advanced().isTimedOut()){
			SessionCloseMsg.onTimeout(sessionId);
		}
		
		System.out.println("close");
		// processor.process(exchange.advanced().isTimedOut() ?
		// SessionCloseMsg.onTimeout(sessionId)
		// : SessionCloseMsg.onError(sessionId));
	}

	public long getTimeout() {
		return timeout;
	}

	public void setSessionType(SessionType sessionType) {
		this.sessionType = sessionType;
	}

	public SessionType getSessionType() {
		return sessionType;
	}

	public int nextSeqNumber() {
		return seqNumber.getAndIncrement();
	}
}
