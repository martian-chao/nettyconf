package com.lyc.protocol;

import com.lyc.protocol.codec.NettyMessageDecoder;
import com.lyc.protocol.codec.NettyMessageEncoder;
import com.lyc.protocol.handler.HeartbeatHandler;
import com.lyc.protocol.handler.LoginRequestHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 客户端
 *
 */
public class ProtocolClient {

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 60, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());

    public void connect(String host, int port) {

        EventLoopGroup acceptGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(acceptGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new NettyMessageDecoder(1024 * 1024 * 1, 4, 4));
                            pipeline.addLast(new NettyMessageEncoder());
                            pipeline.addLast(new ReadTimeoutHandler(50));
                            pipeline.addLast(new LoginRequestHandler());
                            pipeline.addLast(new HeartbeatHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().closeFuture().sync();
            System.out.println("客户端链接断开.");
        } catch (InterruptedException e) {
            acceptGroup.shutdownGracefully();
            System.out.println("客户端断开." + new Date());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("客户端断开." + new Date());
        } finally {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            acceptGroup.shutdownGracefully();
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println("客户端重连..." + new Date());
                    connect("127.0.0.1", 9080);
                }
            });
        }
    }

    public static void main(String[] args) {
        new ProtocolClient().connect("127.0.0.1", 9080);
    }
}
