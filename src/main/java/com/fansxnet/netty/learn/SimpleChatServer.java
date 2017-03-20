package com.fansxnet.netty.learn;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class SimpleChatServer {
	static final int PORT = 8080;   
	public static void main(String[] args) {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);  
        EventLoopGroup workerGroup = new NioEventLoopGroup();  
        try {  
            ServerBootstrap b = new ServerBootstrap();  
            b.group(bossGroup, workerGroup)  
             .channel(NioServerSocketChannel.class)  
             .childHandler(new ChannelInitializer<SocketChannel>() {  
                @Override  
                public void initChannel(SocketChannel ch) throws Exception {  
                    //new LengthFieldBasedFrameDecoder(1024*8*20, 0, 4,0,4) 最大1024*8*20位为接收数据包，从0，长4Byte是数据宽度，然后从0，长4Byte剔除后的byte数据包，传送 到后面的handler链处理  
                	ChannelPipeline pipeline = ch.pipeline();
                	pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
                    pipeline.addLast(new IdleStateHandler(0, 0, 5));
                    pipeline.addLast("decoder", new StringDecoder());
                    pipeline.addLast("encoder", new StringEncoder());
                    pipeline.addLast(new ChatServerHandler());
                }  
             });  
  
            // Bind and start to accept incoming connections.  
            b.bind(PORT).sync().channel().closeFuture().sync();  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        } finally {  
            bossGroup.shutdownGracefully();  
            workerGroup.shutdownGracefully();  
        }  	
	}
}
