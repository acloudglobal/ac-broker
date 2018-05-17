package com.acloudchina.event.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.acloudchina.event.bean.KafkaMessage;
import com.acloudchina.event.common.JsonUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaService {

	@Autowired
	private KafkaTemplate kafkaTemplate;
	
	@Value("${spring.kafka.template.command-req-topic}")
	private String commandReqTopic;

	public void sendTelemetry(KafkaMessage message) {
		log.info("data collect event:{}", message);

		kafkaTemplate.sendDefault(JsonUtil.objectToJson(message));
		log.info("data collect end,endpointId:{}", message.getEndpointId());

	}
	
	public void sendCommandRequest(KafkaMessage message) {
		log.info("record  req :{}", message);

		kafkaTemplate.send(commandReqTopic, JsonUtil.objectToJson(message));
		
		log.info("record req end,endpointId:{}", message.getEndpointId());

	}
}
