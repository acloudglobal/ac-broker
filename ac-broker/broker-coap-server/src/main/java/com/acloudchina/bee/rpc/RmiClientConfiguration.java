package com.acloudchina.bee.rpc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

import lombok.extern.slf4j.Slf4j;

@Configuration
@ConditionalOnProperty(name = "outbound.service.adapter", havingValue = "rpc")
@Slf4j
public class RmiClientConfiguration {

	@Value("${rmi.aconn.address}")
	private String rmiAddress;

	@Bean(name = "dataInvoker")
	public RmiProxyFactoryBean rmiProxyFactoryBean() {
		RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
		try {
			rmiProxyFactoryBean.setServiceUrl("rmi://" + rmiAddress + "/dataInvoker");
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("RPC地址配置错误或者尚未配置:" + e.getMessage());
		}
		rmiProxyFactoryBean.setServiceInterface(DataInvoker.class);
		return rmiProxyFactoryBean;
	}
}
