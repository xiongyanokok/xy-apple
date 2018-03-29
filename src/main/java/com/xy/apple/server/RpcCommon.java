package com.xy.apple.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

/**
 * RPC 接口与服务直接映射关系
 *
 * @author xiongyan
 * @since 1.0.0
 */
public class RpcCommon {
	
	private RpcCommon() {
		
	}

	/**
     * 存放 服务名 与 服务对象 之间的映射关系
     */
    private static Map<String, Object> handlerMap = new ConcurrentHashMap<>();
    
    public static void setHandlerMap(String interfaceName, String serviceVersion, Object serviceRef) {
    	if (StringUtils.isNotEmpty(serviceVersion)) {
    		interfaceName = interfaceName + ":" + serviceVersion;
    	}
    	handlerMap.put(interfaceName, serviceRef);
    }
    
    public static Map<String, Object> getHandlerMap() {
    	return handlerMap;
    }
}
