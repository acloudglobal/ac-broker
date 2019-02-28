package com.acloudchina.coap.outbound.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.acloudchina.coap.common.RedisKeyContant;
import com.acloudchina.coap.outbound.CommandOutboundService;
import com.acloudchina.event.bean.KafkaMessage;
import com.acloudchina.event.common.JsonUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

@Service
@ConditionalOnProperty(name = "outbound.service.adapter", havingValue = "redis")
@Slf4j
public class RedisOutboundService implements CommandOutboundService {

	@Autowired
	Jedis jedis;

	public List<byte[]> getCommand(KafkaMessage msg) {

		String keyRegx = "";
		if("".equals(msg.getSchemaId())){
			keyRegx = msg.getTenantId() + "_" + msg.getEndpointId() + "_"+msg.getSchemaId()+"_";
		}else{
			keyRegx = msg.getTenantId()  + "_" + msg.getEndpointId() + "_";
		}
		Set<String> keys = jedis.keys(keyRegx);
		
		List<byte[]> result = new ArrayList<>();
		for(String key : keys){
			byte[] value = jedis.get(key.getBytes());
			result.add(value);
		}

		return result;
	}
	
	/**
	 * 获取缓存的指令
	 */
	public List<String> getCommand(String tenantId,String endpointId) {
		String key = String.format(RedisKeyContant.COMMAND_COAP_4_TENANTID_ENDPOINTID_KEY, tenantId,endpointId);
		
		long size = jedis.llen(key);
		List<String> result = new ArrayList<>();
		while(size > 0){
			String value = jedis.rpop(key);
			JSONObject dto = JSONObject.parseObject((String)JSON.parse(value));
			if(dto != null){
				long expireTime = dto.getLongValue("expireTime");
				if(expireTime >=0 && expireTime<System.currentTimeMillis()){
					log.warn("指令超时失效：" + value);
				}else{
					result.add(dto.getString("content"));
				}
			}
			size --;
		}
		
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
