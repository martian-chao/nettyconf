package com.lyc.protocol.codec;


import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 解码器
 *
 */
public class MarshallingDecoder {
    private Logger logger = LoggerFactory.getLogger(MarshallingDecoder.class);
    private Unmarshaller unmarshaller;

    public MarshallingDecoder() throws Exception {unmarshaller = new MarshallingCodeCFactory().buildUnmarshaller();}

    public Object decode(ByteBuf in) throws IOException {
        logger.info("MarshallingDecoder-decode");
        try {
            int objectSize = in.readInt();
            if(objectSize==0) return null;
            ChannelBufferByteInput byteInput = new ChannelBufferByteInput(in.slice(in.readerIndex(), objectSize));
            unmarshaller.start(byteInput);
            Object attachment = unmarshaller.readObject();
            unmarshaller.finish();
            //unmarshaller并不会吧position设置到附件后，所有读完后需要把position置为附件结束位置
            in.readerIndex(in.readerIndex() + objectSize);
            return attachment;
        } catch (Exception e) {
            unmarshaller.close();
            System.out.println(e);
        }
        return null;
    }
}
