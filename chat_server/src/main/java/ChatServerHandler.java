import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChatServerHandler extends ChannelInboundHandlerAdapter {

    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelRegistered(ChannelHandlerContext ctx){
        Channel newChannel = ctx.channel();
        System.out.println("[SYSTEM]" + newChannel.remoteAddress().toString() + " joined");
        for (Channel channel : channelGroup) {
            channel.writeAndFlush("[SYSTEM]" + newChannel.remoteAddress().toString() + " joined\n");
        }
        channelGroup.add(newChannel);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        Channel oldChannel = ctx.channel();
        System.out.println("[SYSTEM]" + oldChannel.remoteAddress().toString() + " left");
        for (Channel channel : channelGroup) {
            channel.writeAndFlush("[SYSTEM]" + oldChannel.remoteAddress().toString() + " left\n");
        }
        channelGroup.remove(oldChannel);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel newChannel = ctx.channel();
        System.out.println("[New Client] remote address - ".concat(newChannel.remoteAddress().toString()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String message;
        message = (String)msg;
        if (message.equals("exit")) {
            channelUnregistered(ctx);
        }
        Channel msgSender = ctx.channel();
        System.out.println("[" + msgSender.remoteAddress().toString() + "]" + message);
        System.out.println(channelGroup.size());
        for (Channel channel : channelGroup) {
            channel.writeAndFlush("[" + msgSender.remoteAddress().toString() + "]" + message + "\n");
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
