package com.lyc.demo1.handler;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

        String resultStr = (String)msg;
        // 接收并打印客户端的信息  
        System.out.println("Client said:" + resultStr);  

        // 向客户端发送消息  
        ByteBuf buf = Unpooled.copiedBuffer(resultStr.getBytes());
        ctx.writeAndFlush(buf);
    }  
  
    @Override  
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {  
        ctx.flush();  
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (this==ctx.pipeline().last()){
            System.out.println("用户:"+ctx.channel().id()+" 异常下线");
        }
        ctx.channel().close();
        ctx.close();
    }
}
