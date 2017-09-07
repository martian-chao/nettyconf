package com.lyc.demo1;

import com.lyc.demo1.handler.HelloClientIntHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 聊天工具
 * 客户端
 *
 */
public class HelloClient {
	public void connect(String host, int port) throws Exception {  
        EventLoopGroup workerGroup = new NioEventLoopGroup();  
        try {
            Bootstrap b = new Bootstrap();  
            b.group(workerGroup);  
            b.channel(NioSocketChannel.class);  
            b.option(ChannelOption.SO_KEEPALIVE, true);  
            b.handler(new ChannelInitializer<SocketChannel>() {  
                @Override  
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new StringDecoder());
                    ch.pipeline().addLast(new StringEncoder());
                    ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                    ch.pipeline().addLast(new HelloClientIntHandler());
                }
            });  
//            //ChannelFuture f = b.connect(host, port).sync();
//            //f.channel().closeFuture().sync();
            //手动输入消息
            Channel channel = b.connect(host,port).sync().channel();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true){
                channel.writeAndFlush(reader.readLine()+"\r\n");
            }
        } finally {
            workerGroup.shutdownGracefully();  
        }  
    }
    public static void main(String[] args) throws Exception {

        HelloClient client = new HelloClient();
        client.connect("127.0.0.1", 8090);
    }
}
