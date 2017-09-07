package com.lyc.demo1.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端业务处理
 */
public class HelloClientIntHandler  extends ChannelInboundHandlerAdapter{
	private static Logger logger = LoggerFactory.getLogger(HelloClientIntHandler.class);  
	  
    // 接收server端的消息，并打印出来  
    @Override  
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {  
        logger.info("HelloClientIntHandler.channelRead");  
    	System.out.println("HelloClientIntHandler.channelRead");
       String result =(String)msg;
        System.out.println("Server said:" + new String(result));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (this==ctx.pipeline().last()){
            System.out.println("服务器关闭");
        }
        ctx.channel().close();
        ctx.close();
    }
}
