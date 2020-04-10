package xiaNing.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class TestServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //得到管道
        ChannelPipeline pipeline = ch.pipeline();

        /**
         *
         * HttpServerCodec是netty提供的处理http的编码解码器
         */
        pipeline.addLast("MyHttpServerCodec",new HttpServerCodec());
        //自定义handle
        pipeline.addLast("",new TestHttpServerHandler());
    }
}
