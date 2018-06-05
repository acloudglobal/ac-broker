package com.acloudchina.coap.outbound;

import java.util.List;

import com.acloudchina.event.bean.KafkaMessage;

public interface CommandOutboundService {
	List<byte[]> getCommand(KafkaMessage msg);
	
	String getConfiguration(KafkaMessage msg);
	
	String getProfile(KafkaMessage msg,String keys);
	
	void updateEndpointToObserver(String tenantId,String endpointId);
	
	void removeObserver(String tenantId,String endpointId);
}
