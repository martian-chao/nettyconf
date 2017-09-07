package com.lyc.protocol.handler;

import com.lyc.protocol.vo.Header;
import com.lyc.protocol.vo.MessageType;
import com.lyc.protocol.vo.MyCommConfig;
import com.lyc.protocol.vo.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务器登录返回handler
 * 服务端的握手接入和安全认证
 */
public class LoginResponseHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(LoginResponseHandler.class);
    //已登录的ip容器
    private static ConcurrentHashMap<String, String> onlineNode = new ConcurrentHashMap<>();
    private static final String value = "online";
    private static Set<String> whiteHost = new HashSet<String>() {
        {
            //加载白名单
            //切分本地服务器可以接收的ip
            String localServerAcceptIp = MyCommConfig.localServerAcceptIp;
            String[] localServerAcceptIps = localServerAcceptIp.split(";");
            for (int i = 0; i < localServerAcceptIps.length; i++) {
                localServerAcceptIp=localServerAcceptIps[i];
                add(localServerAcceptIp);
            }
            //白名单中加入本机地址
            add("127.0.0.1");
        }
    };
    private static Set<String> blackHost = new HashSet<String>(){
        {
            //加载黑名单
            String localServerRefuseIp = MyCommConfig.localServerRefuseIp;
            String[] localServerRefuseIps = localServerRefuseIp.split(";");
            for (int i = 0; i < localServerRefuseIps.length; i++) {
                localServerRefuseIp = localServerRefuseIps[i];
                add(localServerRefuseIp);
            }
        }
    };


    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("LoginRespHandler-channelRead");
        NettyMessage request = (NettyMessage) msg;
        //判断是否握手消息
        if (request != null && request.getHeader().getType() == MessageType.ACCEPT_REQ) {
            if (request.getBody() == null || !request.getBody().equals("Hello Netty Private Protocol.")) {
                ctx.close();
            } else {
                System.out.println("进行白名单、黑名单，是否重复连接校验.");
                String address = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
                System.out.println("目前IP地址为："+address);
                if (blackHost.contains(address)) {
                    System.out.println("该IP在黑名单上，禁止访问服务器，断开链接.");
                    ctx.close();
                }
                if (!whiteHost.contains(address)){
                    System.out.println("该IP禁止访问服务器，断开链接.");
                    ctx.close();
                }
                if (onlineNode.containsKey(address)) {
                    System.out.println("该IP重复登录服务器，断开链接.");
                    ctx.close();
                }
                onlineNode.put(address, value);
                NettyMessage response = new NettyMessage();
                Header header = new Header();
                header.setType(MessageType.ACCEPT_RES);
                header.setSessionID(1);
                response.setHeader(header);
                response.setBody("登录成功，返回信息.");
                ctx.writeAndFlush(response);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        onlineNode.remove(((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress());
        System.out.println("关闭链接，释放资源，删除已登录信息防止客户端登录不了.");
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
}