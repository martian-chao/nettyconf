package com.lyc.protocol.handler;

import com.lyc.protocol.vo.Header;
import com.lyc.protocol.vo.MessageType;
import com.lyc.protocol.vo.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 心跳维持
 *
 */
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(HeartbeatHandler.class);
    private ScheduledFuture<?> heartbeatScheduled;
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("HeartbeatHandler-channelRead");
        NettyMessage request = (NettyMessage) msg;
        if (request.getHeader() == null) ctx.fireChannelRead(msg);

        if (request.getHeader().getType() == MessageType.ACCEPT_RES) {
            heartbeatScheduled = ctx.executor().scheduleAtFixedRate(new HeartbeatTask(ctx), 0, 30, TimeUnit.SECONDS);

        } else if (request.getHeader().getType() == MessageType.HEARTBEAT_REQ) {
            NettyMessage response = new NettyMessage();
            response.setHeader(new Header().setType(MessageType.HEARTBEAT_RES));
            response.setBody("心跳回应信息.");
            ctx.writeAndFlush(response);
            System.out.println("收到客户端心跳包,即将返回pong消息..." + request.getBody() + new Date());
        } else if (request.getHeader().getType() == MessageType.HEARTBEAT_RES) {
            System.out.println("心跳正常..." + new Date());
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

    class HeartbeatTask implements Runnable {

        private ChannelHandlerContext ctx;

        public HeartbeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        public void run() {
            logger.info("HeartbeatTask-run");
            NettyMessage heartbeatMsg = new NettyMessage();
            heartbeatMsg.setHeader(new Header().setType(MessageType.HEARTBEAT_REQ));
            heartbeatMsg.setBody("客户端心跳包...");
            ctx.writeAndFlush(heartbeatMsg);
            System.out.println("发送心跳包" + new Date());
        }
    }
}

