package com.xy.apple.registry.zookeeper;

import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.xy.apple.exception.RpcException;
import com.xy.apple.registry.ServiceRegistry;

/**
 * 基于 ZooKeeper 的服务注册接口实现
 * 
 * @author xiongyan
 * @since 1.0.0
 */
public class ZooKeeperServiceRegistry implements ServiceRegistry, InitializingBean, DisposableBean {

	private static final Logger logger = LoggerFactory.getLogger(ZooKeeperServiceRegistry.class);

	private ZkClient zkClient;
	
	private String zookeeperAddress;

	public String getZookeeperAddress() {
		return zookeeperAddress;
	}

	public void setZookeeperAddress(String zookeeperAddress) {
		this.zookeeperAddress = zookeeperAddress;
	}

	
	public void afterPropertiesSet() throws Exception {
		if (StringUtils.isEmpty(zookeeperAddress)) {
			throw new RpcException("zookeeperAddress not found ");
		}
		
		// 创建 ZooKeeper 客户端
		zkClient = new ZkClient(zookeeperAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
		logger.debug("connect zookeeper");
	}
	
	public void register(String serviceName, String serviceAddress) {
		// 创建 registry 节点（持久）
		String registryPath = Constant.ZK_REGISTRY_PATH;
		if (!zkClient.exists(registryPath)) {
			zkClient.createPersistent(registryPath);
			logger.debug("create registry node: {}", registryPath);
		}
		// 创建 service 节点（持久）
		String servicePath = registryPath + "/" + serviceName;
		if (!zkClient.exists(servicePath)) {
			zkClient.createPersistent(servicePath);
			logger.debug("create service node: {}", servicePath);
		}
		// 创建 address 节点（临时）
		String addressPath = servicePath + "/xy-apple_";
		String addressNode = zkClient.createEphemeralSequential(addressPath, serviceAddress);
		logger.debug("create address node: {}", addressNode);
	}
	
	public void destroy() throws Exception {
		if (null != zkClient) {
			zkClient.close();
		}
	}

}