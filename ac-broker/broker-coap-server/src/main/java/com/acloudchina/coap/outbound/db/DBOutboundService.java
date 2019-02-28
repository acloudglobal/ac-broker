package com.acloudchina.coap.outbound.db;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.acloudchina.coap.outbound.CommandOutboundService;
import com.acloudchina.event.bean.KafkaMessage;

@Service("dbOutboundService")
@ConditionalOnProperty(name = "outbound.service.adapter", havingValue = "db")
public class DBOutboundService implements CommandOutboundService {

	@Override
	public List<byte[]> getCommand(KafkaMessage msg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getConfiguration(KafkaMessage msg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProfile(KafkaMessage msg, String keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateEndpointToObserver(String tenantId,String endpointId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeObserver(String tenantId,String endpointId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getCommand(String tenantId, String endpointId) {
		// TODO Auto-generated method stub
		return null;
	}

}
