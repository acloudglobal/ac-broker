package com.acloudchina.coap.inbound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.acloudchina.coap.common.ProtypeEnum;

@Service
public class DataHandleFacade {

	@Autowired
	@Qualifier("commandHandle")
	private DataHandle commandRequestHandle;

	@Autowired
	@Qualifier("dataCollectHandle")
	private DataHandle dataCollectHandle;
	
	@Autowired
	@Qualifier("configurationHandle")
	private DataHandle configurationHandle;

	@Autowired
	@Qualifier("profileHandle")
	private DataHandle profileHandle;
	
	public DataHandle build(ProtypeEnum type) {
		DataHandle handle = null;
		switch (type) {
		case CEP:
			//指令
			handle = commandRequestHandle;
			break;
		case PMP:
			//profile
			handle = profileHandle;
			break;
		case CMP:
			//configuration
			handle = configurationHandle;
			break;
		case DCP:
			//数据采集
			handle = dataCollectHandle;
			break;
		default:
			break;
		}

		return handle;
	}
}
