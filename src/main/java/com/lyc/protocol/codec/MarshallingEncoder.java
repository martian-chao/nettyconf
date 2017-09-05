package com.lyc.protocol.codec;


import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.Marshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @Description： 编码器
 * Created by Ambitor on 2017/4/26.
 */
public class MarshallingEncoder {
    private Logger logger = LoggerFactory.getLogger(MarshallingEncoder.class);
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    private Marshaller marshaller;

    public MarshallingEncoder() throws Exception {
        this.marshaller = MarshallingCodeCFactory.buildMarshaller();
    }

    public void encode(Object attachment, ByteBuf out) throws IOException {
        logger.info("MarshallingEncoder-encode");
        try {
            int lengthPostion = out.writerIndex();
            //空出32位标志附件的长度
            out.writeBytes(LENGTH_PLACEHOLDER);
            ChannelBufferByteOutput output = new ChannelBufferByteOutput(out);
            marshaller.start(output);
            marshaller.writeObject(attachment);
            marshaller.finish();
            //往lengthPostion位置写入附件长度
            out.setInt(lengthPostion, out.writerIndex() - lengthPostion - 4);
        } catch (IOException e) {
            marshaller.close();
        }
    }
}
