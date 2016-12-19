package com.xy.apple.netty;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import com.xy.apple.exception.ParamNotFoundException;
import com.xy.apple.registry.ServiceDiscovery;

public class RpcReferer implements ApplicationContextAware {
	
	private String id;
	
	private String interfaceName;
	
	private String serviceVersion = "";
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getServiceVersion() {
		return serviceVersion;
	}

	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}

	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ServiceDiscovery serviceDiscovery = (ServiceDiscovery) applicationContext.getBean("serviceDiscovery");
		if (null == serviceDiscovery) {
			throw new BeanCreationException("serviceDiscovery", " not found");
		}
		
		if (StringUtils.isEmpty(id)) {
			throw new ParamNotFoundException("id not found ");
		}
		
		if (StringUtils.isEmpty(interfaceName)) {
			throw new ParamNotFoundException("interfaceName not found ");
		}
		
		Class<?> interfaceClass = null;
		try {
			interfaceClass = Class.forName(interfaceName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		// 创建代理类
		Object proxy = RpcProxy.create(interfaceClass, serviceVersion, serviceDiscovery);

		// 将applicationContext转换为ConfigurableApplicationContext
		ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;

		// 获取bean工厂并转换为DefaultListableBeanFactory
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();

		// 注册单例bean
		defaultListableBeanFactory.registerSingleton(id, proxy);
	}

}
