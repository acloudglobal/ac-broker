package com.acloudchina.coap.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.californium.core.observe.ObserveRelation;

public class CacheExchange {

	private static Map<String, ObserveRelation> cache = new ConcurrentHashMap<>();

	public static void put(String key, ObserveRelation ctx) {
		cache.put(key, ctx);
	}

	public static ObserveRelation get(String key) {
		return cache.get(key);
	}

	public static void remove(String key) {
		cache.remove(key);
	}

	public static Map<String, ObserveRelation> all() {
		return cache;
	}

	public static int size() {
		return cache.size();
	}
}
