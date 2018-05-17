package com.acloudchina.coap;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.acloudchina.bee.rpc.PushToClientService;
import com.acloudchina.coap.inbound.DataHandleFacade;
import com.acloudchina.coap.inbound.ObserverDataHandle;
import com.acloudchina.coap.security.AuthService;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class CoapBroker {

	@Autowired
	private AuthService authService;

	@Autowired
	private DataHandleFacade dataHandleFacade;
	@Autowired
	private ObserverDataHandle observerDataHandle;
	@Autowired
	private PushToClientService pushToClientService;

	@Value("${coap.bind_address:localhost}")
	private String host;
	@Value("${coap.bind_port:5683}")
	private Integer port;

	CoapServer server = null;

	private static final String V1 = "v1";

	@PostConstruct
	public void init() throws Exception {
		// 启动server
		startCoapServer(port);
		log.info("Coap Server started at port:" + port);
	}

	@PreDestroy
	public void destory() throws Exception {
		if (server != null) {
			server.stop();
			log.info("Coap Server stoped....");
		}
	}

	public void startCoapServer(int port) {
		server = new CoapServer();
		NetworkConfig.getStandard().setInt(NetworkConfig.Keys.MAX_RETRANSMIT, 2);
		NetworkConfig.getStandard().setInt(NetworkConfig.Keys.MAX_TRANSMIT_WAIT, 10);
		NetworkConfig.getStandard().setInt(NetworkConfig.Keys.ACK_TIMEOUT, 10);
		createResources();
		addEndpoints(port);

		server.start();
	}

	private void createResources() {
		CoapResource api = new CoapTransportResource(V1, authService, dataHandleFacade, observerDataHandle,
				pushToClientService);
		server.add(api);
	}

	private void addEndpoints(int port) {
		for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
			if (addr instanceof Inet4Address || addr.isLoopbackAddress()) {
				InetSocketAddress bindToAddress = new InetSocketAddress(addr, port);
				server.addEndpoint(new CoapEndpoint(bindToAddress));
			}
		}
	}

}
