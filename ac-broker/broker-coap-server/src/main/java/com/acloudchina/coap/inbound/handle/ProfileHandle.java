package com.acloudchina.coap.inbound.handle;

import java.util.List;
import java.util.Map;

import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acloudchina.coap.CoapResponse;
import com.acloudchina.coap.common.FormatTypeEnum;
import com.acloudchina.coap.common.UriPositionDefine;
import com.acloudchina.coap.inbound.DataHandle;
import com.acloudchina.coap.outbound.CommandOutboundService;
import com.acloudchina.coap.outbound.OutboundFacadeService;
import com.acloudchina.event.bean.KafkaMessage;
import com.acloudchina.event.common.ProtocolEnum;
import com.acloudchina.event.kafka.KafkaService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProfileHandle implements DataHandle {

	// <version>/<tenant id>/pmp/<ep id>/get/<payload format>/<schema
	// id>?keys=ac,sc,

	// <version>/<tenant id>/pmp/<ep id>/update/keys/<payload format>/<schema
	// id>

	// <version>/<tenant id>/pmp/<ep id>/delete/keys/<payload format>/<schema
	// id>

	@Autowired
	private KafkaService kafkaService;

	@Autowired
	private OutboundFacadeService outboundFacadeService;

	@Override
	public void handle(CoapExchange exchange, Map<String, String> param) {
		Request request = exchange.advanced().getRequest();

		byte[] payload = request.getPayload();

		List<String> path = request.getOptions().getUriPath();
		if (path.size() < 6) {
			CoapResponse.createAndSendResponse(ResponseCode.BAD_REQUEST, "profile请求路径不正确", exchange);
		} else {
			String version = path.get(UriPositionDefine.VERSION_POSITION);
			String tenantId = path.get(UriPositionDefine.TENANT_POSITION);
			String protype = path.get(UriPositionDefine.PROTYPE_POSITION);
			String endpointId = path.get(UriPositionDefine.ENDPOINT_POSITION);

			String cfgType = path.get(4);

			KafkaMessage msg = new KafkaMessage();
			msg.setEndpointId(endpointId);
			msg.setPayload(payload);
			msg.setTenantId(tenantId);
			msg.setType(1);
			msg.setProtocol(ProtocolEnum.CoAP.name());
			msg.setVersion(version);
			msg.setProtype(protype);
			if ("get".equals(cfgType)) {
				String format = path.get(5);
				String schemaId = "";
				if (!FormatTypeEnum.json.name().equals(format)) {
					if (path.size() < 7) {
						CoapResponse.createAndSendResponse(ResponseCode.BAD_REQUEST, "profile请求路径不正确", exchange);
						return;
					}
					schemaId = path.get(6);
				}
				msg.setAction("get");
				msg.setSchemaId(schemaId);
				processRequest(exchange, request, msg, param);
			}
			if ("update".equals(cfgType)) {
				String format = path.get(6);
				String schemaId = "";
				if (!FormatTypeEnum.json.name().equals(format)) {
					if (path.size() < 8) {
						CoapResponse.createAndSendResponse(ResponseCode.BAD_REQUEST, "profile修改路径不正确", exchange);
						return;
					}
					schemaId = path.get(7);
				}
				msg.setAction("update");
				msg.setSchemaId(schemaId);
				processUpdate(exchange, request, msg);
			}
			if ("delete".equals(cfgType)) {
				String format = path.get(6);
				String schemaId = "";
				if (!FormatTypeEnum.json.name().equals(format)) {
					if (path.size() < 8) {
						CoapResponse.createAndSendResponse(ResponseCode.BAD_REQUEST, "profile删除Key的路径不正确", exchange);
						return;
					}
					schemaId = path.get(7);
				}
				msg.setAction("delete");
				msg.setSchemaId(schemaId);
				processDelete(exchange, request, msg);
			} else {
				CoapResponse.createAndSendResponse(ResponseCode.BAD_REQUEST, "profile路径不正确", exchange);
			}
		}
	}

	private void processDelete(CoapExchange exchange, Request request, KafkaMessage msg) {
		if (Code.DELETE != request.getCode()) {
			CoapResponse.createAndSendResponse(ResponseCode.METHOD_NOT_ALLOWED, "Profile删除key必须使用delete", exchange);
		} else {
			log.info("Profile删除keyURI：" + request.getURI());

			try {
				kafkaService.sendTelemetry(msg);
				CoapResponse.createAndSendResponse(ResponseCode.CONTENT, "success", exchange);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("endpointId:{},profile delete handle error:{}", msg.getEndpointId(), e.getMessage());
				CoapResponse.createAndSendResponse(ResponseCode.INTERNAL_SERVER_ERROR, "delete handle error", exchange);
			}
		}
	}

	private void processUpdate(CoapExchange exchange, Request request, KafkaMessage msg) {
		if (Code.PUT != request.getCode()) {
			CoapResponse.createAndSendResponse(ResponseCode.METHOD_NOT_ALLOWED, "profile修改必须使用Put", exchange);
		} else {
			log.info("profile修改URI：" + request.getURI());

			try {
				kafkaService.sendTelemetry(msg);
				CoapResponse.createAndSendResponse(ResponseCode.CONTENT, "success", exchange);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("endpointId:{},profile update handle error:{}", msg.getEndpointId(), e.getMessage());
				CoapResponse.createAndSendResponse(ResponseCode.INTERNAL_SERVER_ERROR, "update handle error", exchange);
			}
		}
	}

	private void processRequest(CoapExchange exchange, Request request, KafkaMessage msg, Map<String, String> param) {
		if (Code.GET != request.getCode()) {
			CoapResponse.createAndSendResponse(ResponseCode.METHOD_NOT_ALLOWED, "profile查询请求必须使用get", exchange);
		} else {
			log.info("profile配置请求：" + request.getURI());

			try {
				CommandOutboundService cmdOutboundService = outboundFacadeService.build();

				// 获取keys
				String keys = param.get("keys") == null ? "" : param.get("keys").toString();
				// 查询profile
				String profile = cmdOutboundService.getProfile(msg, keys);

				// 将请求记录下来保存为历史
				kafkaService.sendCommandRequest(msg);
				CoapResponse.createAndSendResponse(ResponseCode.CONTENT, profile, exchange);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("endpointId:{},profile request handle error:{}", msg.getEndpointId(), e.getMessage());
				CoapResponse.createAndSendResponse(ResponseCode.INTERNAL_SERVER_ERROR, "profile request handle error",
						exchange);
			}
		}
	}

}
