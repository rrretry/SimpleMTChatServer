package earth.retry.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChatProcessing extends SimpleChannelInboundHandler<String> {
    static final DefaultChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
        System.out.println(channels.size()+" : "+Thread.activeCount());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        for (Channel client: channels)
            client.writeAndFlush(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channels.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+": "+cause.getMessage());//todo mb ignore it or add logger
    }
}
