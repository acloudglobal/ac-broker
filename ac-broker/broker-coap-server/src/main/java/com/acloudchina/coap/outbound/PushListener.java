package com.acloudchina.coap.outbound;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.acloudchina.coap.session.Cache;
import com.acloudchina.coap.session.CoapSessionCtx;
import com.acloudchina.event.bean.KafkaMessage;
import com.acloudchina.event.common.JsonUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PushListener {

	@KafkaListener(topics = "aconn.to.coap.event")
	public void listen(ConsumerRecord<?, ?> record) {
		log.info("listen rec record");
        KafkaMessage message = JsonUtil.jsonToObject((String) record.value(), KafkaMessage.class);
        log.info("listen rec message, endpointId:{}",message.getEndpointId());
        
        String tenant = message.getTenantId();
        String endpointId = message.getEndpointId();
        
        CoapSessionCtx ctx = Cache.get(tenant+"_"+endpointId);
        try {
			ctx.onNotificationMsg(message.getPayload());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("push error:{}",message);
		}
	}
}
