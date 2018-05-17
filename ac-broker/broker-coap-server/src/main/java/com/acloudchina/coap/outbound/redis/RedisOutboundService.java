package com.acloudchina.coap.outbound.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.acloudchina.coap.outbound.CommandOutboundService;
import com.acloudchina.event.bean.KafkaMessage;

import redis.clients.jedis.Jedis;

@Service
@ConditionalOnProperty(name = "outbound.service.adapter", havingValue = "redis")
public class RedisOutboundService implements CommandOutboundService {

	@Autowired
	Jedis jedis;

	@Value("${spring.redis.database}")
	public int dbIndex;

	public List<String> getCommand(KafkaMessage msg) {
		jedis.select(dbIndex);

		String keyRegx = "";
		if("".equals(msg.getSchemaId())){
			keyRegx = msg.getTenantId() + "_" + msg.getEndpointId() + "_"+msg.getSchemaId()+"_";
		}else{
			keyRegx = msg.getTenantId()  + "_" + msg.getEndpointId() + "_";
		}
		Set<String> keys = jedis.keys(keyRegx);

		List<String> result = new ArrayList<>();
		keys.forEach(key -> {
			String value = jedis.get(key);
			result.add(value);
		});

		return result;
	}

	@Override
	public String getConfiguration(KafkaMessage msg) {
		return null;
	}

	@Override
	public String getProfile(KafkaMessage msg, String keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateEndpointToObserver(String tenantId, String endpointId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeObserver(String tenantId, String endpointId) {
		// TODO Auto-generated method stub
		
	}
}
