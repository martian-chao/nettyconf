package com.lyc.protocol;

import com.lyc.protocol.codec.NettyMessageDecoder;
import com.lyc.protocol.codec.NettyMessageEncoder;
import com.lyc.protocol.handler.HeartbeatHandler;
import com.lyc.protocol.handler.LoginResponseHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * 服务端
 *
 */
public class ProtocolServer {

    public void bind(String host, int port) throws Exception{

        EventLoopGroup acceptGroup = new NioEventLoopGroup();
        EventLoopGroup serverGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(acceptGroup, serverGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("NettyMessageDecoder", new NettyMessageDecoder(1024 * 1024 * 1, 4, 4));
                            pipeline.addLast("NettyMessageEncoder", new NettyMessageEncoder());
                            pipeline.addLast("ReadTimeoutHandler",new ReadTimeoutHandler(50));
                            pipeline.addLast("LoginResponseHandler", new LoginResponseHandler());
                            pipeline.addLast("HeartbeatHandler", new HeartbeatHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(host, port).sync();
            System.out.println("服务器启动成功.");
            future.channel().closeFuture().sync();
        }finally {
            acceptGroup.shutdownGracefully();
            serverGroup.shutdownGracefully();
            System.out.println("服务器中止服务.");
        }
    }

    public static void main(String[] args) {
        try {
            new ProtocolServer().bind("127.0.0.1", 9080);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
