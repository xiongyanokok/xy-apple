package com.xy.apple.common.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IpUtils
 */
public class IpUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(IpUtils.class);
	
	private IpUtils() {
		
	}

	public static final String LOCALHOST = "127.0.0.1";

	public static final String ANYHOST = "0.0.0.0";

	private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

	/**
	 * 获取本机IP地址.
	 * 
	 * <p>
	 * 有限获取外网IP地址. 也有可能是链接着路由器的最终IP地址.
	 * </p>
	 * 
	 * @return 本机IP地址
	 */
	public static String getIp() {
		InetAddress address = getLocalAddress0();
		return address == null ? LOCALHOST : address.getHostAddress();
	}

	/**
	 * 获取本机Host名称.
	 * 
	 * @return 本机Host名称
	 */
	public static String getHostName() {
		InetAddress address = getLocalAddress0();
		return address == null ? LOCALHOST : address.getHostName();
	}

	/**
	 * 获取本机地址
	 * 
	 * @return
	 */
	private static InetAddress getLocalAddress0() {
		InetAddress localAddress = null;
		try {
			localAddress = InetAddress.getLocalHost();
			if (isValidAddress(localAddress)) {
				return localAddress;
			}
		} catch (UnknownHostException e) {
			logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
		}
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			if (interfaces != null) {
				while (interfaces.hasMoreElements()) {
					NetworkInterface network = interfaces.nextElement();
					Enumeration<InetAddress> addresses = network.getInetAddresses();
					if (addresses != null) {
						while (addresses.hasMoreElements()) {
							InetAddress address = addresses.nextElement();
							if (isValidAddress(address)) {
								return address;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
		}
		logger.error("Could not get local host ip address, will use 127.0.0.1 instead.");
		return localAddress;
	}

	/**
	 * 验证是否有效地址
	 * 
	 * @param address
	 * @return
	 */
	private static boolean isValidAddress(InetAddress address) {
		if (address == null || address.isLoopbackAddress()) {
			return false;
		}
		String name = address.getHostAddress();
		return (name != null && !ANYHOST.equals(name) && !LOCALHOST.equals(name) && IP_PATTERN.matcher(name).matches());
	}
 
	
}
