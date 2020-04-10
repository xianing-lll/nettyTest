package xiaNing.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

public class NettyByteBuf02 {
    public static void main(String[] args) {
        ByteBuf byteBuf = Unpooled.copiedBuffer("夏宁，你好", CharsetUtil.UTF_8);

        if (byteBuf.hasArray()){
            byte[] array = byteBuf.array();
            String s = new String(array, CharsetUtil.UTF_8);
            System.out.println(s);
            System.out.println(byteBuf.readerIndex());
            int len = byteBuf.readableBytes();
            System.out.println(len);
            int len2 = byteBuf.getByte(15);
            System.out.println("write number："+len2);
        }


    }
}
