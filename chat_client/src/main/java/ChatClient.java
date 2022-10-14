import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.net.InetSocketAddress;
import java.util.Scanner;

public class ChatClient {
    private static final int SERVER_PORT = 10000;
    private final String host;
    private final int port;

    private EventLoopGroup eventLoopGroup;

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException, IllegalStateException {
        Scanner scanner = new Scanner(System.in);
        String message;
        eventLoopGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("client"));

        Bootstrap bootstrap = new Bootstrap().group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.remoteAddress(new InetSocketAddress(host, port));
        bootstrap.handler(new ChatClientInitializer());

        Channel serverChannel = bootstrap.connect().sync().channel();
        ChannelFuture future;
        while(true) {
            message = scanner.nextLine();
            System.out.println("input: "+ message);
            future = serverChannel.writeAndFlush(message.concat("\n"));

            if (message.equals("exit")) {
                serverChannel.closeFuture().sync();
                break;
            }
        }
        if (future != null) {
            future.sync();
        }
    }

    private void close() {
        eventLoopGroup.shutdownGracefully();
    }
    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient("127.0.0.1", SERVER_PORT);
        try{
            client.start();
        } finally {
            client.close();
        }
    }
}
