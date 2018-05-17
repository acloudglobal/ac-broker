package com.acloudchina.coap.outbound.rpc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.stereotype.Service;

import com.acloudchina.bee.rpc.DataInvoker;
import com.acloudchina.coap.outbound.CommandOutboundService;
import com.acloudchina.event.bean.KafkaMessage;

@Service
@ConditionalOnProperty(name = "outbound.service.adapter", havingValue = "rpc")
public class RmiOutboundService implements CommandOutboundService {

	@Autowired
	@Qualifier("dataInvoker")
	private RmiProxyFactoryBean rmiProxyFactoryBean;

	@Override
	public List<String> getCommand(KafkaMessage msg) {
		DataInvoker dataInvoker = (DataInvoker) rmiProxyFactoryBean.getObject();
		return dataInvoker.getCommands(msg.getTenantId(), msg.getEndpointId(), msg.getSchemaId());
	}

	@Override
	public String getConfiguration(KafkaMessage msg) {
		DataInvoker dataInvoker = (DataInvoker) rmiProxyFactoryBean.getObject();
		return dataInvoker.getConfiguration(msg.getTenantId(), msg.getEndpointId(), msg.getSchemaId());
	}

	@Override
	public String getProfile(KafkaMessage msg, String keys) {
		DataInvoker dataInvoker = (DataInvoker) rmiProxyFactoryBean.getObject();
		return dataInvoker.getProfile(msg.getTenantId(), msg.getEndpointId(), msg.getSchemaId());
	}

	@Override
	public void updateEndpointToObserver(String tenantId, String endpointId) {
		DataInvoker dataInvoker = (DataInvoker) rmiProxyFactoryBean.getObject();	
		dataInvoker.updateEndpointToObserver(tenantId, endpointId);
	}

	@Override
	public void removeObserver(String tenantId, String endpointId) {
		DataInvoker dataInvoker = (DataInvoker) rmiProxyFactoryBean.getObject();	
		dataInvoker.removeObserver(tenantId, endpointId);		
	}

}
