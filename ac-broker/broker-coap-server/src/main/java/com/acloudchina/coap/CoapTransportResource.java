package com.acloudchina.coap;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;
import org.springframework.util.ReflectionUtils;

import com.acloudchina.bee.rpc.PushToClientService;
import com.acloudchina.coap.common.ParamUtils;
import com.acloudchina.coap.common.ProtypeEnum;
import com.acloudchina.coap.common.UriPositionDefine;
import com.acloudchina.coap.inbound.DataHandle;
import com.acloudchina.coap.inbound.DataHandleFacade;
import com.acloudchina.coap.inbound.ObserverDataHandle;
import com.acloudchina.coap.security.AuthService;
import com.acloudchina.coap.session.Cache;
import com.acloudchina.coap.session.CacheExchange;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CoapTransportResource extends CoapResource {

	private AuthService authService;
	private DataHandleFacade dataHandleFacade;
	private ObserverDataHandle observerDataHandle;
	private Field observerField;
	private PushToClientService pushToClientService;

	public CoapTransportResource(String name, AuthService authService, DataHandleFacade dataHandleFacade,
			ObserverDataHandle observerDataHandle, PushToClientService pushToClientService) {
		super(name);

		 getAttributes().setObservable();
		 setObserveType(Type.CON);
		setObservable(true);

		this.authService = authService;
		this.dataHandleFacade = dataHandleFacade;
		this.observerDataHandle = observerDataHandle;
		this.pushToClientService = pushToClientService;

		observerField = ReflectionUtils.findField(Exchange.class, "observer");
		observerField.setAccessible(true);

		addObserver(new CoapResourceObserver());
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.setMaxAge(1000);
		OptionSet optionSet = exchange.getRequestOptions();
		List<String> paths = optionSet.getUriPath();
		Optional<ProtypeEnum> featureType = getProType(paths);
		
		processRequest(exchange, paths, featureType.get());

//		if (exchange.getRequestOptions().hasObserve()) {
//			observerDataHandle.handleObser(exchange, paths, observerField, timeout);
//		} else {
//			processRequest(exchange, paths, featureType.get());
//		}
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		List<String> paths = exchange.getRequestOptions().getUriPath();
		Optional<ProtypeEnum> featureType = getProType(paths);

		processRequest(exchange, paths, featureType.get());
	}

	@Override
	public void handleDELETE(CoapExchange exchange) {
		List<String> paths = exchange.getRequestOptions().getUriPath();
		Optional<ProtypeEnum> featureType = getProType(paths);

		processRequest(exchange, paths, featureType.get());
	}

	@Override
	public void handlePUT(CoapExchange exchange) {
		List<String> paths = exchange.getRequestOptions().getUriPath();
		Optional<ProtypeEnum> featureType = getProType(paths);

		processRequest(exchange, paths, featureType.get());
	}

	private void processRequest(CoapExchange exchange, List<String> paths, ProtypeEnum featureType) {
		if (paths == null || paths.size() < 5) {
			log.error("请求路径不正确:" + exchange.getRequestOptions().getUriPathString());
			CoapResponse.createAndSendResponse(ResponseCode.BAD_REQUEST, "请求路径不正确", exchange);
		} else {
			List<String> paramList = exchange.getRequestOptions().getUriQuery();
			Map<String, String> jo = ParamUtils.transelateParam(paramList, paths);

			log.info("uri:" + exchange.getRequestOptions().getUriPathString() + ",param:" + jo);

			boolean result = authService.validate(jo);
			if (result) {
				DataHandle dataHandle = dataHandleFacade.build(featureType);
				if (dataHandle != null) {
					dataHandle.handle(exchange, jo);
				} else {
					log.warn("请求未找到类型");
					CoapResponse.createAndSendResponse(ResponseCode.BAD_REQUEST, "请求类型不存在", exchange);
				}
			} else {
				CoapResponse.createAndSendResponse(ResponseCode.UNAUTHORIZED, "认证失败", exchange);
			}

		}
	}

	private Optional<ProtypeEnum> getProType(List<String> uriPath) {
		try {
			if (uriPath.size() >= UriPositionDefine.PROTYPE_POSITION + 1) {
				return Optional.of(ProtypeEnum.valueOf(uriPath.get(UriPositionDefine.PROTYPE_POSITION).toUpperCase()));
			}
		} catch (RuntimeException e) {
			log.error("Failed to decode feature type: {}", uriPath);
		}
		return Optional.empty();
	}

	@Override
	public Resource getChild(String name) {
		return this;
	}
}
