package com.lyc.protocol.vo;

/**
 * 心跳消息，握手请求，握手应答
 * 消息头和消息体
 */
public class NettyMessage {
	private Header header;
	private Object body;
	public Header getHeader() {
		return header;
	}
	public void setHeader(Header header) {
		this.header = header;
	}
	public Object getBody() {
		return body;
	}
	public void setBody(Object body) {
		this.body = body;
	}
	
	public String toString(){
		return "NettyMessage [header=" + header + "]";
	}
}
