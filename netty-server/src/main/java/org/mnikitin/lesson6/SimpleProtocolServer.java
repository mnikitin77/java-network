package org.mnikitin.lesson6;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.mnikitin.lesson6.handler.SimpleProtocolHandler;
import org.mnikitin.lesson6.service.SimpleProtocolServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleProtocolServer {

    private static final Logger log = LoggerFactory.getLogger(SimpleProtocolServer.class);
    private static final int DEFAULT_PORT = 12345;

    public static void main(String[] args) throws InterruptedException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        new SimpleProtocolServer(port);
    }

    public SimpleProtocolServer(int port) throws InterruptedException {

        var bossGroup = new NioEventLoopGroup();
        var workerGroup = new NioEventLoopGroup();

        try {
            var b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    new StringDecoder(),
                                    new StringEncoder(),
                                    new SimpleProtocolHandler(new SimpleProtocolServiceImpl())
                            );
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();
            log.info("SimpleProtocolServer started on port {}.", port);
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.info("SimpleProtocolServer shut down gracefully.");
        }
    }
}
