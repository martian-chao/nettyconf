package com.lyc.demo1.handler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
/**
 * 服务端处理业务
 *  *
 */
public class HelloServerInHandler extends ChannelInboundHandlerAdapter{
	private static Logger logger = LoggerFactory  
            .getLogger(HelloServerInHandler.class);  
  
    @Override  
    public void channelRead(ChannelHandlerContext ctx, Object msg)  
            throws Exception {  
        logger.info("HelloServerInHandler.channelRead");  
//        System.out.println("HelloServerInHandler.channelRead");
        ByteBuf result = (ByteBuf) msg;  
        //result.readableBytes()未读的消息大小
        byte[] result1 = new byte[result.readableBytes()];  
        // msg中存储的是ByteBuf类型的数据，把数据读取到byte[]中  
        result.readBytes(result1);  
        String resultStr = new String(result1);  
        // 接收并打印客户端的信息  
        System.out.println("Client said:" + resultStr);  
        // 释放资源，这行很关键  
        result.release();  
  
        // 向客户端发送消息  
        String response = "I am ok!";
        ByteBuf buf = Unpooled.copiedBuffer(response.getBytes());
        ctx.writeAndFlush(buf);
    }  
  
    @Override  
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {  
        ctx.flush();  
    }  
}
