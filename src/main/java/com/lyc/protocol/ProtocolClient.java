package com.lyc.protocol;

import com.lyc.protocol.codec.NettyMessageDecoder;
import com.lyc.protocol.codec.NettyMessageEncoder;
import com.lyc.protocol.handler.HeartbeatHandler;
import com.lyc.protocol.handler.LoginRequestHandler;
import com.lyc.protocol.vo.MyCommConfig;
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
    private int count=0;//重连计数器
    private int retryCountClientSend;//客户端重连次数 10次
    private long intervalClientConnector;//重新连接服务端间隔时间 30秒
    private int aliveTimeoutClientSend;//接收服务端响应超时时间 5秒

    public ProtocolClient(){
        this.retryCountClientSend=MyCommConfig.retryCountClientSend;
        this.intervalClientConnector=MyCommConfig.intervalClientConnector;
        this.aliveTimeoutClientSend=MyCommConfig.aliveTimeoutClientSend;
    }

    private ThreadPoolExecutor executor =
            new ThreadPoolExecutor(1, 1, 60, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());

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
                            //设置超时时间
                            pipeline.addLast(new ReadTimeoutHandler(aliveTimeoutClientSend));
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
                //间隔30秒重连
                Thread.sleep(intervalClientConnector);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            acceptGroup.shutdownGracefully();
            //重连10次
            if(count<retryCountClientSend){
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        count++;
                        System.out.println("客户端重连...第" +count+"次，"+ new Date());
                        connect("127.0.0.1", 8002);
                    }
                });
            }
        }
    }

    public static void main(String[] args) {
        new ProtocolClient().connect("127.0.0.1", 8002);
    }
}
