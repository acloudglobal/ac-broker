package com.acloudchina.event.bean;

import lombok.Data;

@Data
public class KafkaMessage {
	private String tenantId;
	private String endpointId;
	private byte[] payload;
	private String nodeName;
	private String format;
	private String version;
	private String schemaId;
	private String protype;
	private String protocol;
	private String action;  //create/update/delete/get
	
	// 1:app 2: sys connect 3: sys disconnected
	private Integer type;

	private String clientId;

	private String topic;
}
