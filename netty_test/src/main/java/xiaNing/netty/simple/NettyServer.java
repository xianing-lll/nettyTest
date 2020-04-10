package xiaNing.netty.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * netty服务器
 */
public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        //创建bossgroup和workergroup
        /**
         * 说明
         * 创建两个线程组bossgroup和workergroup
         * bossgroup只负责连接，workergroup真正负责处理客户端业务
         * 两个都是无限循环
         * bossgroup和workergroup默认线程数为cpu核数*2
         */
        EventLoopGroup bossgroup = new NioEventLoopGroup();
        EventLoopGroup workergroup = new NioEventLoopGroup();

        try {
            //创建服务器端的启动对象，配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();

            //使用链式编程进行设置
            bootstrap.group(bossgroup,workergroup)//设置连个线程组
                    .channel(NioServerSocketChannel.class) //使用使用NioSocketChannel作为服务器的通道实现作为服务器的通道实现
                    .option(ChannelOption.SO_BACKLOG,128) //设置线程队列得到线程个数
                    .childOption(ChannelOption.SO_KEEPALIVE,true) //设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //给pipeline设置处理器

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            System.out.println("客户端hascode:"+socketChannel.hashCode());
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    }); //给我们的workergroup的EventLoop对应的管道设置处理器

            System.out.println("...服务器正在准备...");
            //绑定一个6668端口，并同步创建一个ChannelFuture对象
            ChannelFuture channelFuture = bootstrap.bind(6668).sync();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()){
                        System.out.println("监听6668端口成功！");
                    }else {
                        System.out.println("listening fail");
                    }
                }
            });
            //对关闭通道进行监听
            //启动服务器并绑定端口
            channelFuture.channel().closeFuture().sync();

        }finally {
            bossgroup.shutdownGracefully();
            workergroup.shutdownGracefully();
        }

    }
}
