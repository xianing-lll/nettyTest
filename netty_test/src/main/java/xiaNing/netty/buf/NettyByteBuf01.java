package xiaNing.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NettyByteBuf01 {
    public static void main(String[] args) {
        /**
         * 创建一个bytebuf
         */
        ByteBuf buffer = Unpooled.buffer(10);
        ByteBuf buffer1 = Unpooled.buffer();
        System.out.println(buffer.readableBytes());
        System.out.println(buffer1.readableBytes());
        for (int i = 0; i < 10; i++) {
            buffer.writeByte(i);
        }

        buffer1 = buffer.copy();
        System.out.println("capacity"+buffer.capacity());
        for (int i = 0; i < buffer.capacity(); i++) {
            byte b = buffer.getByte(i);
            System.out.println(buffer.getByte(i));
        }

        for (int i = 0; i <buffer1.capacity() ; i++) {
            System.out.println(buffer1.getByte(i));
        }
        System.out.println(buffer1.capacity());
    }
}
