package com.lyc.protocol.codec;

import com.lyc.protocol.vo.Header;
import com.lyc.protocol.vo.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 私有协议解码
 *
 */
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {
    private Logger logger = LoggerFactory.getLogger(NettyMessageDecoder.class);

    private MarshallingDecoder marshallingDecoder;

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) throws Exception {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        marshallingDecoder = new MarshallingDecoder();
    }

    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        logger.info("NettyMessageDecoder-decode");
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        //这种情况一般是半包的时候
        if (frame == null) return null;

        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setCrcCode(frame.readInt());
        header.setLength(frame.readInt());
        header.setSessionID(frame.readLong());
        header.setType(frame.readByte());
        header.setPriority(frame.readByte());

        int attachmentSize = frame.readInt();
        Map<String, Object> attachment = new HashMap<String, Object>();
        for (int i = 0; i < attachmentSize; i++) {
            int keySize = frame.readInt();
            byte[] keyByte = new byte[keySize];
            frame.readBytes(keyByte);
            String key = new String(keyByte, "UTF-8");
            String a = new String();
            Object value = marshallingDecoder.decode(frame);
            attachment.put(key, value);
        }
        header.setAttachment(attachment);
        message.setHeader(header);
        Object body = marshallingDecoder.decode(frame);
        message.setBody(body);
        return message;
    }
}
