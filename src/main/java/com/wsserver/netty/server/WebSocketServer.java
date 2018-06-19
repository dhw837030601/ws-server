package com.wsserver.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component("WebSocketServer")
public class WebSocketServer {

	private final ChannelGroup group = new DefaultChannelGroup(
			ImmediateEventExecutor.INSTANCE);

	private final EventLoopGroup workerGroup = new NioEventLoopGroup();

	@Autowired
	private ChatServerInitializer csi;

	private Channel channel;

	public ChannelFuture start(InetSocketAddress address) {
		ServerBootstrap boot = new ServerBootstrap();
		boot.group(workerGroup).channel(NioServerSocketChannel.class)
				.childHandler(csi);

		ChannelFuture f = boot.bind(address).syncUninterruptibly();
		channel = f.channel();
		return f;
	}



	public void destroy() {
		if (channel != null)
			channel.close();
		group.close();
		workerGroup.shutdownGracefully();
	}

	public static void main(String[] args) {
		final WebSocketServer server = new WebSocketServer();
		ChannelFuture f = server.start(new InetSocketAddress(2048));
		System.out.println("server start................");
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				server.destroy();
			}
		});
		f.channel().closeFuture().syncUninterruptibly();
	}




	public void running() {

		String port =  "18080";// 获取端口号
		this.start(new InetSocketAddress(Integer.valueOf(port)));
		System.out.println("----------------------------------------WEBSOCKET SERVER START----------------------------------------");


	}
}