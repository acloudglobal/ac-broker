package com.acloudchina.bee.rpc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiServiceExporter;

@Configuration
public class RmiServerConfiguration {

	@Value("${rmi.server.port}")
	private int rmiPort;

	/* rmi 服务器暴漏 服务 */
	@Bean
	public RmiServiceExporter rmiServiceExporter(PushToClientService pushToClientService) {
		RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
		// 客户端通过rmi调用的端口
		rmiServiceExporter.setRegistryPort(rmiPort);
		// 客户端调用注册调用的服务名
		rmiServiceExporter.setServiceName("pushToClient");
		// 注册的service
		rmiServiceExporter.setService(pushToClientService);
		// 注册的接口
		rmiServiceExporter.setServiceInterface(PushToClientService.class);
		return rmiServiceExporter;
	}

	
}
