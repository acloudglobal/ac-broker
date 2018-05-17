package com.acloudchina.bee.rpc.impl;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.observe.ObserveRelation;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.springframework.stereotype.Service;

import com.acloudchina.bee.rpc.PushToClientService;
import com.acloudchina.coap.CoapResponse;
import com.acloudchina.coap.session.Cache;
import com.acloudchina.coap.session.CacheExchange;
import com.acloudchina.coap.session.CoapSessionCtx;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PushToClientServiceImpl implements PushToClientService {

	private CoapResource res;

	@Override
	public boolean pushCmdToClient(String tenant, String endpoint, String cmd) {
		// CoapSessionCtx ctx = Cache.get(tenant + "_" + endpoint);
		// try {
		// if(ctx!=null){
		// ctx.onNotificationMsg(cmd);
		// return true;
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// log.error("Tenant:{},endpoint:{},推送数据错误：{}", tenant, endpoint,
		// e.getMessage());
		// }
		// return false;
		String key = tenant + "_" + endpoint;
		ObserveRelation ctx = CacheExchange.get(key);
		try {
			if (ctx != null) {
				System.out.println("push key:" + key);
				ctx.notifyObservers();
			}
			// CacheExchange.all().forEach((x,ctx) -> {
			// System.out.println(ctx.getKey());
			// ctx.notifyObservers();
			// });
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Tenant:{},endpoint:{},推送数据错误：{}", tenant, endpoint, e.getMessage());
		}
		return false;
	}

	@Override
	public boolean pushProfileToClient(String tenant, String endpoint, String profile) {
		CoapSessionCtx ctx = Cache.get(tenant + "_" + endpoint);
		try {
			ctx.onNotificationMsg(profile);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Tenant:{},endpoint:{},推送数据错误：{}", tenant, endpoint, e.getMessage());
			return false;
		}
	}

	@Override
	public boolean pushConfigToClient(String tenant, String endpoint, String config) {
		CoapSessionCtx ctx = Cache.get(tenant + "_" + endpoint);
		try {
			ctx.onNotificationMsg(config);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Tenant:{},endpoint:{},推送数据错误：{}", tenant, endpoint, e.getMessage());
			return false;
		}
	}

	@Override
	public void initResource(CoapResource res) {
		this.res = res;
	}

}
