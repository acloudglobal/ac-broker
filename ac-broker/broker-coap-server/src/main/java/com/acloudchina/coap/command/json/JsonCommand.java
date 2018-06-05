package com.acloudchina.coap.command.json;

import java.util.List;

import lombok.Data;

@Data
public class JsonCommand {

	private int count;
	private List<byte[]> commands;
	private long responseTime;
}
