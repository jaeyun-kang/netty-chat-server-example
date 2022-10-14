import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetSocketAddress;

public class ChatServer {
    private static final int SERVER_PORT = 10000;
    private final ChannelGroup allChannels = new DefaultChannelGroup("server", GlobalEventExecutor.INSTANCE);
    private EventLoopGroup masterEventLoopGroup;
    private EventLoopGroup workerEventLoopGroup;

    public void startServer() {
        masterEventLoopGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("master"));
        workerEventLoopGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("worker"));

        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(masterEventLoopGroup, workerEventLoopGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.localAddress(new InetSocketAddress(SERVER_PORT));
        bootstrap.childHandler(new ChatServerInitializer());

        try {
            ChannelFuture bindFuture = bootstrap.bind().sync();
            Channel channel = bindFuture.channel();
            allChannels.add(channel);
            System.out.println("Server Activated\n");
            bindFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            closeServer();
            System.out.println("Server Closed\n");
        }
    }

    public void closeServer() {
        allChannels.close().awaitUninterruptibly();
        workerEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
        masterEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
    }

    public static void main(String[] args) {
        new ChatServer().startServer();
    }
}
