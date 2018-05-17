package com.acloudchina.coap.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {

	private static Map<String, CoapSessionCtx> cache = new ConcurrentHashMap<>();

	public static void put(String key, CoapSessionCtx ctx) {
		cache.put(key, ctx);
	}

	public static CoapSessionCtx get(String key) {
		return cache.get(key);
	}

	public static void remove(String key) {
		cache.remove(key);
	}

	public static Map<String, CoapSessionCtx> all() {
		return cache;
	}

	public static int size() {
		return cache.size();
	}
}
