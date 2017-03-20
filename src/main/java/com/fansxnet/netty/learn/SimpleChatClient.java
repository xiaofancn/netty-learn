package com.fansxnet.netty.learn;

import java.util.concurrent.CountDownLatch;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class SimpleChatClient {

	private final String host;
	private final int port;

	public SimpleChatClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void run() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			SimpleChatClientInitializer clientInitializer = new SimpleChatClientInitializer();
			Bootstrap bootstrap = new Bootstrap().group(group).channel(NioSocketChannel.class)
					.handler(clientInitializer);
			ChannelFuture connect = bootstrap.connect(host, port);
			Channel channel = connect.sync().channel();
			CountDownLatch lathc ;
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 10; i++) {
				sb.append("中华人民共和国。");
			}
			for (int i = 0; i < 1000; i++) {
				String sendData = i + ":"+sb.toString();
				lathc = new CountDownLatch(1);// 此处为控制同步的关键信息，注意此对象的流转
				clientInitializer.resetLathc(lathc);
				channel.write(sendData);
				System.out.println(i);
				channel.flush();
				lathc.await();
				System.out.println(clientInitializer.getServerResult());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// group.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		new SimpleChatClient("127.0.0.1", 8080).run();
	}
}