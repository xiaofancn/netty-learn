package com.fansxnet.netty.learn;

import java.util.concurrent.CountDownLatch;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class SimpleChatClientInitializer extends ChannelInitializer<SocketChannel> {

    private CountDownLatch lathc;

     

    private SimpleChatClientHandler handler;

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        handler =  new SimpleChatClientHandler(lathc);
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new IdleStateHandler(0, 0, 5));
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());
        pipeline.addLast("handler", handler);
    }

    public String getServerResult(){
        return handler.getResult();
    }
    //重置同步锁
    public void resetLathc(CountDownLatch initLathc) {
        handler.resetLatch(initLathc);
    }

}