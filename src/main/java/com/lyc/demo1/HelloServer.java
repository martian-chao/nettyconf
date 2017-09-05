package com.lyc.demo1;

import com.lyc.demo1.handler.HelloServerInHandler;
import com.lyc.demo1.handler.LoginRespHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by yanChaoLiu on 2017/9/2.
 */
public class HelloServer {

    public void bind(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HelloServerInHandler());
                            ch.pipeline().addLast(new LoginRespHandler());

                        }
                    }).option(ChannelOption.SO_BACKLOG,100)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("服务器启动成功.");
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            System.out.println("服务器中止服务.");
        }
    }

    public static void main(String[] args) {
        MainApp.initMyCommConfig("a.properties");
        MainApp.start();
    }


}
