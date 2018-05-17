package com.acloudchina.coap.inbound;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.network.ExchangeObserver;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.acloudchina.coap.CoapResponse;
import com.acloudchina.coap.common.ParamUtils;
import com.acloudchina.coap.common.UriPositionDefine;
import com.acloudchina.coap.outbound.CommandOutboundService;
import com.acloudchina.coap.outbound.OutboundFacadeService;
import com.acloudchina.coap.security.AuthService;
import com.acloudchina.coap.session.Cache;
import com.acloudchina.coap.session.CacheExchange;
import com.acloudchina.coap.session.CoapExchangeObserverProxy;
import com.acloudchina.coap.session.CoapSessionCtx;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ObserverDataHandle {

	@Autowired
	private AuthService authService;

	@Autowired
	private OutboundFacadeService outboundFacadeService;

	@Value("${outbound.service.adapter}")
	private String outboundType;

	public void handleObser(CoapExchange exchange, List<String> paths, Field observerField, long timeout) {
		if (paths == null || paths.size() < 5) {
			log.error("请求路径不正确:" + exchange.getRequestOptions().getUriPathString());
			CoapResponse.createAndSendResponse(ResponseCode.BAD_REQUEST, "请求路径不正确", exchange);
		} else {
			List<String> paramList = exchange.getRequestOptions().getUriQuery();
			Map<String, String> jo = ParamUtils.transelateParam(paramList, paths);

			log.info("uri:" + exchange.getRequestOptions().getUriPathString() + ",param:" + jo);

			boolean result = authService.validate(jo);
			if (result) {
				int obs = exchange.advanced().getRequest().getOptions().getObserve();
				String tenantId = paths.get(UriPositionDefine.TENANT_POSITION);
				String endpointId = paths.get(UriPositionDefine.ENDPOINT_POSITION);

				if (obs == 1) {
					processCloseObser(exchange, tenantId, endpointId);
					return;
				}
				try {
					CoapSessionCtx ctx = new CoapSessionCtx(exchange, timeout);
					String key = tenantId + "_" + endpointId;
					Cache.put(key, ctx);

					Exchange advanced = exchange.advanced();
					ExchangeObserver systemObserver = (ExchangeObserver) observerField.get(advanced);

					advanced.setObserver(new CoapExchangeObserverProxy(systemObserver, ctx));

					// 提交修改终端状态
					CommandOutboundService cmdOutboundService = outboundFacadeService.build();
					cmdOutboundService.updateEndpointToObserver(tenantId, endpointId);
				} catch (Exception e) {
					e.printStackTrace();
					exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR, e.getMessage());
				}
			} else {
				CoapResponse.createAndSendResponse(ResponseCode.UNAUTHORIZED, "认证失败", exchange);
			}
		}
	}
	
	
	public void handleObser(CoapExchange exchange, List<String> paths) {
		if (paths == null || paths.size() < 5) {
			log.error("请求路径不正确:" + exchange.getRequestOptions().getUriPathString());
			CoapResponse.createAndSendResponse(ResponseCode.BAD_REQUEST, "请求路径不正确", exchange);
		} else {
			List<String> paramList = exchange.getRequestOptions().getUriQuery();
			Map<String, String> jo = ParamUtils.transelateParam(paramList, paths);

			log.info("uri:" + exchange.getRequestOptions().getUriPathString() + ",param:" + jo);

			boolean result = authService.validate(jo);
			if (result) {
				int obs = exchange.advanced().getRequest().getOptions().getObserve();
				String tenantId = paths.get(UriPositionDefine.TENANT_POSITION);
				String endpointId = paths.get(UriPositionDefine.ENDPOINT_POSITION);

				if (obs == 1) {
					processCloseObser(exchange, tenantId, endpointId);
					return;
				}
				try {
					String key = tenantId + "_" + endpointId;
//					CacheExchange.put(key, exchange);


					// 提交修改终端状态
					CommandOutboundService cmdOutboundService = outboundFacadeService.build();
					cmdOutboundService.updateEndpointToObserver(tenantId, endpointId);
				} catch (Exception e) {
					e.printStackTrace();
					exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR, e.getMessage());
				}
			} else {
				CoapResponse.createAndSendResponse(ResponseCode.UNAUTHORIZED, "认证失败", exchange);
			}
		}
	}

	public void processCloseObser(CoapExchange exchange, String tenantId, String endpointId) {
		String key = tenantId + "_" + endpointId;
		Cache.remove(key);
		// 提交修改终端状态
		CommandOutboundService cmdOutboundService = outboundFacadeService.build();
		cmdOutboundService.removeObserver(tenantId, endpointId);

		exchange.respond(ResponseCode.VALID);
	}
}
