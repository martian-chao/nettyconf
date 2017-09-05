package com.lyc.protocol.handler;

import com.lyc.protocol.vo.Header;
import com.lyc.protocol.vo.MessageType;
import com.lyc.protocol.vo.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端握手、权限handler
 *  * 通道激活时发起握手请求
 */
public class LoginRequestHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(LoginRequestHandler.class);
    private ConcurrentHashMap<String, String> onlineNode = new ConcurrentHashMap<>();
    private final String value = "online";
    private Set<String> whiteHost = new HashSet<String>() {
        {
            //加载白名单
            add("127.0.0.1");
        }
    };

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("LoginRequestHandler-channelActive");
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.ACCEPT_REQ);
        message.setHeader(header);
        message.setBody("Hello Netty Private Protocol.");
        ctx.writeAndFlush(message);
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("LoginRequestHandler-channelRead");
        NettyMessage request = (NettyMessage) msg;
        //如果是握手应答消息，需要判断是否认证成功
        if (request != null && request.getHeader().getType() == MessageType.ACCEPT_RES) {
            if (request.getHeader().getSessionID() != 1) {
                ctx.close();
            }
        }
        ctx.fireChannelRead(msg);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        onlineNode.remove(ctx.channel().remoteAddress().toString());
        ctx.fireExceptionCaught(cause);
    }
}
