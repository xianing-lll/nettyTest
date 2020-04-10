package xiaNing.netty.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * netty心跳检测
 */
public class MyServer {
    public static void main(String[] args) throws Exception{
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            /**
                             * 1、IdleStateHandler是netty提供的处理空闲状态的处理器
                             * 2、三个参数分别为多长时间没有读，多长时间没有写，多长时间没有读和写，
                             * 然后就会发送一个心跳检测包检测是否连接
                             * 3、当IdleStateEvent出发后，就会传递给管道的下一个handler去处理
                             * 通过调用（触发）下一个handler的userEventTiggered,在该方法中去处理
                             * IdleStateEvent读写异常
                             */
                            pipeline.addLast(new IdleStateHandler(3,
                                    5,7, TimeUnit.SECONDS));
                            //对空闲进一步将检测的包,自定义
                            pipeline.addLast(new MyServerHandler());
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(7000).sync();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                    if (future.isSuccess()){
                        System.out.println("服务器已启动>>>>>>");
                    }else {
                        System.out.println("启动失败");
                    }
                }
            });
            channelFuture.channel().closeFuture().sync();
        }finally {

            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
