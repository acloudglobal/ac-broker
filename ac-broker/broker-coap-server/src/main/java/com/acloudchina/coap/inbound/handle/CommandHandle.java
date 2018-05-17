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
import com.acloudchina.coap.outbound.CommandTranslate;
import com.acloudchina.coap.outbound.OutboundFacadeService;
import com.acloudchina.event.bean.KafkaMessage;
import com.acloudchina.event.common.ProtocolEnum;
import com.acloudchina.event.kafka.KafkaService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommandHandle implements DataHandle {
	// <version>/<tenant id>/cep/<ep id>/command/<payload format>
	// <version>/<tenant id>/cep/<ep id>/result/<payload format>

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
			CoapResponse.createAndSendResponse(ResponseCode.BAD_REQUEST, "指令请求路径不正确", exchange);
		} else {
			String version = path.get(UriPositionDefine.VERSION_POSITION);
			String tenantId = path.get(UriPositionDefine.TENANT_POSITION);
			String protype = path.get(UriPositionDefine.PROTYPE_POSITION);
			String endpointId = path.get(UriPositionDefine.ENDPOINT_POSITION);

			String cmdType = path.get(4);
			String format = path.get(5);

			String schemaId = "";
			if (!FormatTypeEnum.json.name().equals(format)) {
				if (path.size() < 7) {
					CoapResponse.createAndSendResponse(ResponseCode.BAD_REQUEST, "指令请求路径不正确", exchange);
					return;
				}
				schemaId = path.get(6);
			}

			KafkaMessage msg = new KafkaMessage();
			msg.setEndpointId(endpointId);
			msg.setPayload(payload);
			msg.setTenantId(tenantId);
			msg.setType(1);
			msg.setProtocol(ProtocolEnum.CoAP.name());
			msg.setVersion(version);
			msg.setFormat(format);
			msg.setSchemaId(schemaId);
			msg.setProtype(protype);

			if ("result".equals(cmdType)) {
				processResultResponse(exchange, request, msg);
			} else {
				processRequest(exchange, request, msg);
			}
		}
	}

	private void processResultResponse(CoapExchange exchange, Request request, KafkaMessage msg) {
		if (Code.POST != request.getCode()) {
			CoapResponse.createAndSendResponse(ResponseCode.METHOD_NOT_ALLOWED, "指令执行结果上报必须使用post", exchange);
		} else {
			log.info("指令响应结果URI：" + request.getURI());

			try {
				kafkaService.sendTelemetry(msg);
				CoapResponse.createAndSendResponse(ResponseCode.CONTENT, "result received", exchange);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("endpointId:{},command request handle error:{}", msg.getEndpointId(), e.getMessage());
				CoapResponse.createAndSendResponse(ResponseCode.NOT_ACCEPTABLE, "request handle error", exchange);
			}
		}
	}

	private void processRequest(CoapExchange exchange, Request request, KafkaMessage msg) {
		if (Code.GET != request.getCode()) {
			CoapResponse.createAndSendResponse(ResponseCode.METHOD_NOT_ALLOWED, "指令查询请求必须使用get", exchange);
		} else {
			log.info("指令请求：" + request.getURI());

			try {

				CommandOutboundService cmdOutboundService = outboundFacadeService.build();
				// 查询指令
				List<String> commands = cmdOutboundService.getCommand(msg);
				byte[] responsePayload = CommandTranslate.transeToPayload(msg.getFormat(), commands);

				// 将请求记录下来保存为历史
				kafkaService.sendCommandRequest(msg);
				CoapResponse.createAndSendResponse(ResponseCode.CONTENT, responsePayload, exchange);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("endpointId:{},command request handle error:{}", msg.getEndpointId(), e.getMessage());
				CoapResponse.createAndSendResponse(ResponseCode.NOT_ACCEPTABLE, "request handle error", exchange);
			}
		}
	}

}
