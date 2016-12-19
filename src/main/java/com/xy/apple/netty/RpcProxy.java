package com.xy.apple.netty;

import java.lang.reflect.Method;
import java.util.UUID;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xy.apple.common.bean.RpcRequest;
import com.xy.apple.common.bean.RpcResponse;
import com.xy.apple.registry.ServiceDiscovery;

/**
 * RPC 代理（用于创建 RPC 服务代理）
 *
 * @author xiongyan
 * @since 1.0.0
 */
public class RpcProxy {

    private static final Logger logger = LoggerFactory.getLogger(RpcProxy.class);

    /**
     * 创建代理类
     * 
     * @param interfaceClass
     * @param serviceVersion
     * @param serviceDiscovery
     * @return
     */
    @SuppressWarnings("unchecked")
	public static <T> T create(final Class<?> interfaceClass, final String serviceVersion, final ServiceDiscovery serviceDiscovery) {
    	Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(interfaceClass);
        enhancer.setCallback(new MethodInterceptor() {
			
			public Object intercept(Object object, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
				// 创建 RPC 请求对象并设置请求属性
				String serviceName = interfaceClass.getName();
		        RpcRequest request = new RpcRequest();
		        request.setRequestId(UUID.randomUUID().toString());
		        request.setInterfaceName(serviceName);
		        request.setServiceVersion(serviceVersion);
		        request.setMethodName(method.getName());
		        request.setParameterTypes(method.getParameterTypes());
		        request.setParameters(objects);
		        
		        // 获取 RPC 服务地址
		        String serviceAddress = null;
		        if (serviceDiscovery != null) {
		            if (StringUtils.isNotEmpty(serviceVersion)) {
		                serviceName += "-" + serviceVersion;
		            }
		            // 获取服务器地址
		            serviceAddress = serviceDiscovery.discover(serviceName);
		            logger.debug("discover service: {} => {}", serviceName, serviceAddress);
		        }
		        if (StringUtils.isEmpty(serviceAddress)) {
		            throw new RuntimeException("server address is empty");
		        }
		        // 从 RPC 服务地址中解析主机名与端口号
		        String[] array = StringUtils.split(serviceAddress, ":");
		        String host = array[0];
		        int port = Integer.parseInt(array[1]);
		        // 创建 RPC 客户端对象并发送 RPC 请求
		        RpcClient client = new RpcClient(host, port);
		        long time = System.currentTimeMillis();
		        // 调用RPC服务器端
		        RpcResponse response = client.send(request);
		        logger.debug("time: {}ms", System.currentTimeMillis() - time);
		        if (response == null) {
		            throw new RuntimeException("response is null");
		        }
		        // 返回 RPC 响应结果
		        if (response.hasException()) {
		            throw response.getException();
		        } else {
		            return response.getResult();
		        }
			}
		});
        return (T) enhancer.create();
    }

}
