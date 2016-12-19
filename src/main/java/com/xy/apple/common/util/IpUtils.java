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

    /**
     * ip环境变量的keys
     */
    static String[] args = new String[]{"x-forwarded-for", "Proxy-Client-IP", "X-Forwarded-For", "WL-Proxy-Client-IP", "X-Real-IP"};
    
    public static final String LOCALHOST = "127.0.0.1";
    
    public static final String ANYHOST = "0.0.0.0";
    
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    
	public static InetAddress getLocalAddress0() {
		InetAddress localAddress = null;
		try {
			localAddress = InetAddress.getLocalHost();
			if (isValidAddress(localAddress)) {
				return localAddress;
			}
		} catch (Throwable e) {
			logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
		}
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			if (interfaces != null) {
				while (interfaces.hasMoreElements()) {
					try {
						NetworkInterface network = interfaces.nextElement();
						Enumeration<InetAddress> addresses = network.getInetAddresses();
						if (addresses != null) {
							while (addresses.hasMoreElements()) {
								try {
									InetAddress address = addresses.nextElement();
									if (isValidAddress(address)) {
										return address;
									}
								} catch (Throwable e) {
									logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
								}
							}
						}
					} catch (Throwable e) {
						logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
					}
				}
			}
		} catch (Throwable e) {
			logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
		}
		logger.error("Could not get local host ip address, will use 127.0.0.1 instead.");
		return localAddress;
	} 
	
	private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress())
            return false;
        String name = address.getHostAddress();
        return (name != null 
                && ! ANYHOST.equals(name)
                && ! LOCALHOST.equals(name) 
                && IP_PATTERN.matcher(name).matches());
    }
	
	public static String getServerIp() {
		InetAddress address = getLocalAddress0();
        return address == null ? LOCALHOST : address.getHostAddress(); 
	}
	
	public static String getLocalIp() {
		InetAddress inetAddress = null;
		try {
			inetAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		byte[] address = inetAddress.getAddress();
		if (null != address && address.length > 0) {
			StringBuffer localIp = new StringBuffer();
			for (byte ip : address) {
				if (localIp.length() > 0) {
					localIp.append(".");
				}
				localIp.append(ip & 0xFF);
			}
			return localIp.toString();
		}
		return null;
	}   
	
	public static void main(String[] args) {
		System.out.println(getServerIp());
		System.out.println(getLocalIp());
	}
}
