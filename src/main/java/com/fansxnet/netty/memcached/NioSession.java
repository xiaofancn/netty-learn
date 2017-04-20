package com.fansxnet.netty.memcached;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheClientCodec;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheObjectAggregator;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheOpcodes;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheRequest;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheResponse;
import io.netty.handler.codec.memcache.binary.DefaultBinaryMemcacheRequest;
import io.netty.handler.codec.memcache.binary.DefaultFullBinaryMemcacheRequest;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

public class NioSession {
	private MemcachedProtocol protocol;
	private Channel channel;
	private Bootstrap boot;

	public NioSession(String host, int port) throws InterruptedException {
		protocol = new MemcachedProtocol();
		boot = new Bootstrap().group(new NioEventLoopGroup()).channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true).remoteAddress(new InetSocketAddress(host, port))
				.handler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG), new BinaryMemcacheClientCodec(),
								new BinaryMemcacheObjectAggregator(Integer.MAX_VALUE), protocol);
					}

				});

		channel = boot.connect().sync().channel();
	}

	void send(String keyString, String value) throws InterruptedException {
		ByteBuf key = Unpooled.wrappedBuffer(keyString.getBytes(CharsetUtil.UTF_8));
		ByteBuf content = Unpooled.wrappedBuffer(value.getBytes(CharsetUtil.UTF_8));
		ByteBuf extras = channel.alloc().buffer(8);
		extras.writeZero(8);
		DefaultFullBinaryMemcacheRequest req = new DefaultFullBinaryMemcacheRequest(key, extras, content);
		req.setOpcode(BinaryMemcacheOpcodes.SET);
		Cmd cmd = new Cmd();
		cmd.setRequ(req);
		channel.writeAndFlush(cmd);
		cmd.getRequ();
	}
 

	public BinaryMemcacheResponse get(String keyString) throws InterruptedException, ExecutionException {
		ByteBuf key = Unpooled.wrappedBuffer(keyString.getBytes(CharsetUtil.UTF_8));
		BinaryMemcacheRequest req = new DefaultBinaryMemcacheRequest(key);
		req.setOpcode(BinaryMemcacheOpcodes.GET);
		Cmd cmd = new Cmd();
		cmd.setRequ(req);
		channel.writeAndFlush(cmd);
		return cmd.getResp();
	}

	public void close() {
		if (channel != null && channel.isActive()) {
			channel.close();
		}
		if( protocol != null ) {
			protocol.wakeUpAll() ;
			protocol = null ;
		}
		channel = null;
		if (boot != null && boot.config() != null) {
			boot.config().group().shutdownGracefully();
		}
		boot = null;
	}
}
