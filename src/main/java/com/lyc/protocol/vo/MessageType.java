package com.lyc.protocol.vo;

/**
 * Netty协议 消息类型
 */

public class MessageType {

    public final static byte SERVICE_REQ = 0;//业务请求消息
    public final static byte SERVICE_RES = 1;//业务响应消息
    public final static byte SERVICE_ONE_WAY = 2;//业务one way (既请求又响应消息)
    public final static byte ACCEPT_REQ = 3;//握手请求消息
    public final static byte ACCEPT_RES = 4;//握手应答消息
    public final static byte HEARTBEAT_REQ = 5;//心跳请求消息
    public final static byte HEARTBEAT_RES = 6;//心跳应答消息

}
