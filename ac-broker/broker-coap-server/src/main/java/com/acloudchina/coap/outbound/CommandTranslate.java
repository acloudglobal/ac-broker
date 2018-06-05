package com.acloudchina.coap.outbound;

import java.util.List;

import com.acloudchina.coap.command.json.JsonCommand;
import com.acloudchina.coap.common.FormatTypeEnum;
import com.acloudchina.event.common.JsonUtil;

public class CommandTranslate {

	public static byte[] transeToPayload(String format, List<byte[]> cmds) {
		if (FormatTypeEnum.json.name().equals(format)) {
			return handleJson(cmds);
		}
		return null;
	}

	private static byte[] handleJson(List<byte[]> cmds) {
		JsonCommand jc = new JsonCommand();
		jc.setCommands(cmds);
		jc.setCount(cmds.size());
		jc.setResponseTime(System.currentTimeMillis());

		return JsonUtil.encode(jc);
	}
}
