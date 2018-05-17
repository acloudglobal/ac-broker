package com.acloudchina.coap.outbound;

public enum OutboundTypeEnum {
	http((String) "http"), redis((String) "redis"), rpc((String) "rpc"), db((String) "db");

	private String val;

	OutboundTypeEnum(String val) {
		this.val = val;
	}

	public String getVal() {
		return val;
	}

	public static OutboundTypeEnum convertEnum(String val) {
		if (http.getVal().equals(val)) {
			return http;
		} else if (redis.getVal().equals(val)) {
			return redis;
		} else if (rpc.getVal().equals(val)) {
			return rpc;
		} else if (db.getVal().equals(val)) {
			return db;
		} else {
			throw new IllegalArgumentException("处理类型非法");
		}
	}
}
