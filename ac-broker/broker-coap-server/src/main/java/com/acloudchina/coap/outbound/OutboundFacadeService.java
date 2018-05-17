package com.acloudchina.coap.outbound;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.acloudchina.coap.common.ApplicationUtil;

@Service
public class OutboundFacadeService {

	@Value("${outbound.service.adapter}")
	private String outboundType;

	public CommandOutboundService build() {
		OutboundTypeEnum type = OutboundTypeEnum.convertEnum(outboundType);
		CommandOutboundService service = null;
		switch (type) {
		case http:
			break;
		case redis:
			service = ApplicationUtil.getBean("redisOutboundService", CommandOutboundService.class);
			break;
		case db:
			service = ApplicationUtil.getBean("dbOutboundService", CommandOutboundService.class);
			break;
		case rpc:
			service = ApplicationUtil.getBean("rmiOutboundService", CommandOutboundService.class);
			break;
		default:
			break;
		}
		return service;
	}
}
