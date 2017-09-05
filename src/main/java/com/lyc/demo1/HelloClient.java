package com.lyc.demo1;

import com.lyc.demo1.handler.HelloClientIntHandler;
import com.lyc.demo1.vo.MyCommConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
/**
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
                    ch.pipeline().addLast(new HelloClientIntHandler());
                }  
            });  
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } finally {  
            workerGroup.shutdownGracefully();  
        }  
  
    }  
    public static void main(String[] args) throws Exception {
        MainApp.initMyCommConfig("a.properties");
        String[] clientPortGroups = MyCommConfig.lastClientPort.split("，");
        for (int i=0;i< clientPortGroups.length;i++){
            int port=Integer.parseInt(clientPortGroups[i].split(",")[i]);
            String localServerAcceptIp = MyCommConfig.localServerAcceptIp;
            String[] localServerAcceptIps = localServerAcceptIp.split(";");
            //取第一个
            localServerAcceptIp = localServerAcceptIps[i];
            try {
                new HelloClient().connect(localServerAcceptIp,port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /*HelloClient client = new HelloClient();
        client.connect("127.0.0.1", 8090);*/
    }
}
