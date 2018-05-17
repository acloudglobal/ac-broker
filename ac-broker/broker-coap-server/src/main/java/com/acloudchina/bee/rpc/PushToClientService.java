package com.acloudchina.bee.rpc;

import org.eclipse.californium.core.CoapResource;

public interface PushToClientService {

	public void initResource(CoapResource res);
	public boolean pushCmdToClient(String tenant,String endpoint,String cmd);
	public boolean pushProfileToClient(String tenant,String endpoint,String profile);
	public boolean pushConfigToClient(String tenant,String endpoint,String config);
}
