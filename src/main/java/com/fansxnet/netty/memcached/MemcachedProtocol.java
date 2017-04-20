package com.fansxnet.netty.memcached;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.memcache.binary.FullBinaryMemcacheResponse;

public class MemcachedProtocol extends ChannelDuplexHandler {

	private BlockingQueue<Cmd> queue = new LinkedTransferQueue<Cmd>();
	

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Cmd c = queue.take();
		c.setResp((FullBinaryMemcacheResponse) msg);
	}
	
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		Cmd c = (Cmd) msg;
		queue.add(c);
		ctx.writeAndFlush(c.getRequ(), promise);
	}
	
	public void wakeUpAll() {
		if(queue.size()>0)queue.clear();
		this.queue = null ;
	}
	
}
