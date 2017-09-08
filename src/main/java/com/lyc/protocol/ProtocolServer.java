package com.lyc.protocol;

import com.lyc.protocol.codec.NettyMessageDecoder;
import com.lyc.protocol.codec.NettyMessageEncoder;
import com.lyc.protocol.handler.HeartbeatHandler;
import com.lyc.protocol.handler.LoginResponseHandler;
import com.lyc.protocol.vo.MyCommConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 服务端
 *
 */
public class ProtocolServer {
    private int count=0;//重连计数器
    private int retryCountServerSend;//服务端重连次数
    private long intervalServerConnector;//重新连接客户端间隔时间
    private int aliveTimeoutSrvSend;//接收服务端响应超时时间
    private ThreadPoolExecutor executor =
            new ThreadPoolExecutor(1, 1, 60, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
    public ProtocolServer(){
        this.retryCountServerSend=MyCommConfig.retryCountServerSend;
        this.intervalServerConnector=MyCommConfig.intervalServerConnector;
        this.aliveTimeoutSrvSend=MyCommConfig.aliveTimeoutSrvSend;
    }


    public void bind(int port) throws Exception{

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
                            //设置超时时间
                            pipeline.addLast("ReadTimeoutHandler",new ReadTimeoutHandler(aliveTimeoutSrvSend));
                            pipeline.addLast("LoginResponseHandler", new LoginResponseHandler());
                            pipeline.addLast("HeartbeatHandler", new HeartbeatHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind( port).sync();
//            Channel channel = bootstrap.bind( port).sync().channel();

            System.out.println("服务器端口："+port+"绑定成功！");
            future.channel().closeFuture().sync();
//            channel.closeFuture().sync().channel();
        }finally {
            acceptGroup.shutdownGracefully();
            serverGroup.shutdownGracefully();
            System.out.println("服务器中止服务.");
        }
    }

    public static void main(String[] args) {
        try {
            new ProtocolServer().bind(8002);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
