package com.acloudchina.bee.rpc;

import java.util.List;

public interface DataInvoker {

	public List<byte[]> getCommands(String tenantId,String endpointId,String schemaId);
	public String getConfiguration(String tenantId,String endpointId,String schemaId);
	public String getProfile(String tenantId,String endpointId,String schemaId);
	public String updateEndpointToObserver(String tenantId,String endpointId);
	public String removeObserver(String tenantId,String endpointId);
}
