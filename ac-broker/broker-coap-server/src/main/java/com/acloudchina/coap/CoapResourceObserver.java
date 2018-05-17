package com.acloudchina.coap;

import java.util.List;

import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.observe.ObserveRelation;
import org.eclipse.californium.core.server.resources.Resource;
import org.eclipse.californium.core.server.resources.ResourceObserver;

import com.acloudchina.coap.common.UriPositionDefine;
import com.acloudchina.coap.session.CacheExchange;

public class CoapResourceObserver implements ResourceObserver {

	@Override
	public void changedName(String old) {
		// TODO Auto-generated method stub

	}

	@Override
	public void changedPath(String old) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addedChild(Resource child) {
		// TODO Auto-generated method stub
		System.out.println("childAdd:" + child.getName() + "," + child.getURI());
	}

	@Override
	public void removedChild(Resource child) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addedObserveRelation(ObserveRelation relation) {
		Request request = relation.getExchange().getRequest();
		List<String> path = request.getOptions().getUriPath();

		String tenantId = path.get(UriPositionDefine.TENANT_POSITION);
		String endpointId = path.get(UriPositionDefine.ENDPOINT_POSITION);
		String key = tenantId + "_" + endpointId;

		if (CacheExchange.get(key) != null) {
			removedObserveRelation(relation);
		}
		CacheExchange.put(key, relation);
	}

	@Override
	public void removedObserveRelation(ObserveRelation relation) {
		Exchange exchange = relation.getExchange();

		List<String> uriPath = exchange.getRequest().getOptions().getUriPath();
		String tenantId = uriPath.get(UriPositionDefine.TENANT_POSITION);
		String endpointId = uriPath.get(UriPositionDefine.ENDPOINT_POSITION);
		String key = tenantId + "_" + endpointId;

		CacheExchange.remove(key);
	}

}
