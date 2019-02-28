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
import com.acloudchina.event.bean.KafkaMessage;
import com.acloudchina.event.common.EBAddressConstant;
import com.acloudchina.event.common.JsonUtil;
import com.acloudchina.event.common.ProtocolEnum;
import com.acloudchina.event.kafka.KafkaService;

import lombok.extern.slf4j.Slf4j;

@Service("forwardHandle")
@Slf4j
public class DataCollectTranseparentHandle implements DataHandle {
	@Autowired
	private KafkaService kafkaService;
	
	@Autowired
	CommandOutboundService redisOutboundService;

	public void handle(CoapExchange exchange,Map<String, String> param) {
		Request request = exchange.advanced().getRequest();
		if (Code.POST != request.getCode()) {
			log.error("请求地址method不正确");
			CoapResponse.createAndSendResponse(ResponseCode.METHOD_NOT_ALLOWED, "数据上报必须使用post", exchange);
		} else {
			byte[] payload = request.getPayload();

			List<String> path = request.getOptions().getUriPath();
			if (path.size() < 5) {
				CoapResponse.createAndSendResponse(ResponseCode.BAD_REQUEST, "数据上报路径不正确", exchange);
			} else {
				
				System.out.println("===forward");
				String version = path.get(UriPositionDefine.VERSION_POSITION);
				String tenantId = path.get(UriPositionDefine.TENANT_POSITION);
//				String protype = path.get(UriPositionDefine.PROTYPE_POSITION);
				String endpointId = path.get(UriPositionDefine.ENDPOINT_POSITION);
//				String format = path.get(4);
//
//				String schemaId = "";
//				if (!FormatTypeEnum.json.name().equals(format)) {
//					if(path.size() < 6){
//						CoapResponse.createAndSendResponse(ResponseCode.BAD_REQUEST, "指令请求路径不正确", exchange);
//						return;
//					}
//					schemaId = path.get(5);
//				}
//
				KafkaMessage msg = new KafkaMessage();
				msg.setEndpointId(endpointId);
				msg.setPayload(payload);
				msg.setTenantId(tenantId);
				msg.setType(4);   //临时作为非存储使用
//				msg.setSchemaId(schemaId);
//				msg.setProtype(protype);
				msg.setProtocol(ProtocolEnum.CoAP.name());
				msg.setVersion("v3");
//				msg.setFormat(format);
//
				log.info("kafkamessage:" + msg);
//
				try {
					kafkaService.sendTelemetry(msg,EBAddressConstant.DATA_COLLECT_ADDR_TEST);
					
					//查询指令信息
					List<String> cmdList = redisOutboundService.getCommand(tenantId, endpointId);
					String returnCmd = JsonUtil.objectToJson(cmdList);
					
					CoapResponse.createAndSendResponse(ResponseCode.CREATED, returnCmd, exchange);
				} catch (Exception e) {
					e.printStackTrace();
					log.error("endpointId:{},command request handle error:{}", msg.getEndpointId(), e.getMessage());
					CoapResponse.createAndSendResponse(ResponseCode.NOT_ACCEPTABLE, "data collect failed", exchange);
				}

			}
		}
	}
}
