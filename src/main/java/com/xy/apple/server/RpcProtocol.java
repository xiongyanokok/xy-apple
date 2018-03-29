package com.xy.apple.server;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.xy.apple.common.bean.RpcRequest;
import com.xy.apple.common.bean.RpcResponse;
import com.xy.apple.common.codec.RpcDecoder;
import com.xy.apple.common.codec.RpcEncoder;
import com.xy.apple.common.util.IpUtils;
import com.xy.apple.exception.RpcException;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * RPC 协议
 *
 * @author xiongyan
 * @since 1.0.0
 */
public class RpcProtocol implements InitializingBean, ApplicationListener<ContextRefreshedEvent> {
	
	private static final Logger logger = LoggerFactory.getLogger(RpcProtocol.class);
	
	private String ip;
	
    private Integer port;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	
	public void afterPropertiesSet() throws Exception {
		if (null == port || port <= 0) {
			port = 8080;
		}
		
		// 获取服务器ip地址
		ip = IpUtils.getIp();
		
		if (StringUtils.isEmpty(ip)) {
			throw new RpcException("get ip error");
		}
	}


	public void onApplicationEvent(ContextRefreshedEvent event) {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建并初始化 Netty 服务端 Bootstrap 对象
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

            	public void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new RpcDecoder(RpcRequest.class)); // 解码 RPC 请求
                    pipeline.addLast(new RpcEncoder(RpcResponse.class)); // 编码 RPC 响应
                    pipeline.addLast(new RpcServerHandler()); // 处理 RPC 请求
                }
            });
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            // 启动 RPC 服务器
            ChannelFuture future = bootstrap.bind(ip, port).sync();
            logger.debug("server started on ip {}, port {}", ip, port);
            // 关闭 RPC 服务器
            future.channel().closeFuture().sync();
        } catch (Exception e) {
			logger.error("server started error ", e);
		} finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
	}

}
