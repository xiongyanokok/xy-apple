package com.xy.apple.registry.zookeeper;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xy.apple.registry.ServiceDiscovery;

/**
 * 基于 ZooKeeper 的服务发现接口实现
 * 
 * @author xiongyan
 * @since 1.0.0
 */
public class ZooKeeperServiceDiscovery implements ServiceDiscovery {

	private static final Logger logger = LoggerFactory.getLogger(ZooKeeperServiceDiscovery.class);

	private String zookeeperAddress;

	public String getZookeeperAddress() {
		return zookeeperAddress;
	}

	public void setZookeeperAddress(String zookeeperAddress) {
		this.zookeeperAddress = zookeeperAddress;
	}

	public String discover(String serviceName) {
		// 创建 ZooKeeper 客户端
		ZkClient zkClient = new ZkClient(zookeeperAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
		logger.debug("connect zookeeper");
		try {
			// 获取 service 节点
			String servicePath = Constant.ZK_REGISTRY_PATH + "/" + serviceName;
			if (!zkClient.exists(servicePath)) {
				throw new RuntimeException(String.format("can not find any service node on path: %s", servicePath));
			}
			List<String> addressList = zkClient.getChildren(servicePath);
			if (null == addressList || addressList.size() == 0) {
				throw new RuntimeException(String.format("can not find any address node on path: %s", servicePath));
			}
			// 获取 address 节点
			String address;
			int size = addressList.size();
			if (size == 1) {
				// 若只有一个地址，则获取该地址
				address = addressList.get(0);
				logger.debug("get only address node: {}", address);
			} else {
				// 若存在多个地址，则随机获取一个地址
				address = addressList.get(ThreadLocalRandom.current().nextInt(size));
				logger.debug("get random address node: {}", address);
			}
			// 获取 address 节点的值
			String addressPath = servicePath + "/" + address;
			return zkClient.readData(addressPath);
		} finally {
			zkClient.close();
		}
	}
}