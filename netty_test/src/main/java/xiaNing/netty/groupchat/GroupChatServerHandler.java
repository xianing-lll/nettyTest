package xiaNing.netty.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;


public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    //定义一个channel组，管理所有chanel
    //GlobalEventExecutor.INSTANCE:全局事件执行器，单例
    private static ChannelGroup channelGroup=
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * handlerAdded 表示连接建立，一旦链接，第一个被执行
     * 2、将当前chanel加入channelGroup
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //channelgroup会自动将消息发送给所有channel
        channelGroup.writeAndFlush(simpleDateFormat.format(new Date())+"[客户端]"+channel.remoteAddress()+" 加入聊天\n");
        //将客户端唯一channel加入到channel组
        channelGroup.add(channel);
    }

    //表示channel处于活动状态，提示×××上线
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("     "+simpleDateFormat.format(new Date())+
                "["+ctx.channel().remoteAddress()+"]"+"上线了");
    }
    //表示channel处于非活动状态，提示×××离线
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("     "+simpleDateFormat.format(new Date())+
                "["+ctx.channel().remoteAddress()+"]"+"离线了");
    }

    //将××客户离开推送给在线客户
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //channelgroup会自动将消息发送给所有channel
        channelGroup.writeAndFlush(simpleDateFormat.format(new Date())+"[客户端]"+channel.remoteAddress()+" 离开了\n");
        //只要激活该方法，Chanel自动从chanlgroup中删除
        System.out.println("在线人数："+channelGroup.size());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.forEach(channel1 -> {
            if (channel!=channel1){
                channel1.writeAndFlush(simpleDateFormat.format(new Date())+"[客户]"+channel1.remoteAddress()+" 发送了消息:"+msg+"\n");
            }else { //回显
                channel1.writeAndFlush(simpleDateFormat.format(new Date())+"[自己]发送了消息:"+msg);
            }
        });
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        ctx.close();
    }
}
