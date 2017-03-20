package com.fansxnet.netty.learn;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		ctx.writeAndFlush("收到:"+msg.substring(0,msg.indexOf(':')));
	}

	 

}
