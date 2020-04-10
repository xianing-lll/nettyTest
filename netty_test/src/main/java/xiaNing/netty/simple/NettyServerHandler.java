package xiaNing.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * 自定义handler需要继承netty规定好的某个HandlerAdapter
 * 这是我们自定义一个handler，才能成为一个handler、
 *
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    //读取客户端发送数据

    /**
     *
     * @param ctx：上下文对象，含有管道pipeline，通道channel，地址
     * @param msg：就是客户端发送的数据 默认类型Object
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("服务器使用线程名："+Thread.currentThread().getName()+"  "+Thread.currentThread().getId());
//        System.out.println("server ctx="+ctx);
//
//        //将msg装化成由netty提供的ByteBuf
//        ByteBuf buf = (ByteBuf) msg;
//        System.out.println("客户端发送的消息是："+buf.toString(CharsetUtil.UTF_8));
//        System.out.println("客户端地址："+ctx.channel().remoteAddress());

        //耗时解决1、当有耗时任务时可以自定义普通任务
        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try{
                    //加入这里有一个很耗时操作
                    Thread.sleep(10*1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("喵喵1,客户端你好，我们已经连接上了",CharsetUtil.UTF_8));
                }catch (Exception ex){
                    System.out.println("have a except"+ex.getMessage());
                }

            }
        });

        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(20*1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("喵喵2,客户端你好，我们已经连接上了",CharsetUtil.UTF_8));

                }catch (Exception ex){
                    System.out.println(ex.getMessage());
                }

            }
        });

        //解决2：用户自定义定时任务，该任务提交到scheduleTaskqueue

        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(20*1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("喵喵3,客户端你好，我们已经连接上了",CharsetUtil.UTF_8));

                }catch (Exception ex){
                    System.out.println(ex.getMessage());
                }

            }
        }, 30, TimeUnit.SECONDS);
        System.out.println("go on。。。");
    }

    //读取客户端数据后
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端~",CharsetUtil.UTF_8));
    }

    //处理异常，一般需要关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
