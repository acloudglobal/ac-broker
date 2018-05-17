package com.acloudchina.coap.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationUtil implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		applicationContext = context;
	}

	public static <T> T getBean(String name, Class<T> clazz) {
		if (applicationContext == null) {
			return null;
		}

		return applicationContext.getBean(name, clazz);
	}
}
