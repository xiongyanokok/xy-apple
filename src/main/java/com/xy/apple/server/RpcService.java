package com.xy.apple.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.xy.apple.registry.ServiceRegistry;

/**
 * rpc 服务
 * 
 * @author xiongyan
 * @date 2016年12月16日 上午11:09:25
 */
public class RpcService implements ApplicationContextAware {
	
	private static final Logger logger = LoggerFactory.getLogger(RpcService.class);
	
	private String interfaceName;
	
	private String serviceVersion = "";
	
	private Object interfaceService;
	
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

	public Object getInterfaceService() {
		return interfaceService;
	}

	public void setInterfaceService(Object interfaceService) {
		this.interfaceService = interfaceService;
	}

	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		RpcProtocol rpcProtocol = (RpcProtocol) applicationContext.getBean("rpcProtocol");
		ServiceRegistry serviceRegistry = (ServiceRegistry) applicationContext.getBean("serviceRegistry");
		
		// rpc接口与实现类之间的映射关系
		RpcCommon.setHandlerMap(interfaceName, serviceVersion, interfaceService);
		
		// 注册 RPC 服务地址
        if (serviceRegistry != null) {
            // 服务器 ip地址和端口号
            String serviceAddress = rpcProtocol.getIp() + ":" + rpcProtocol.getPort();
            serviceRegistry.register(interfaceName, serviceAddress);
            logger.debug("register service: {} => {}", interfaceName, serviceAddress);
        }
	}

}
