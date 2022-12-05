package org.mnikitin.lesson6;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class SimpleProtocolClient {

    private static final Logger log = LoggerFactory.getLogger(SimpleProtocolClient.class);

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        var host = "127.0.0.1";
        var port = 12345;
        new SimpleProtocolClient(port, host);
    }

    public SimpleProtocolClient(int port, String host) throws InterruptedException {

        var workerGroup = new NioEventLoopGroup();
        try {
            var b = new Bootstrap();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    new StringDecoder(),
                                    new StringEncoder(),
                                    new ClientHandler()
                            );
                        }
                    });
            var f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Error on starting SimpleProtocolClient");
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public class ClientHandler extends SimpleChannelInboundHandler<String> {

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            ctx.writeAndFlush("5:hello");
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String s) {
            log.info("Received response from server: {}", s);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.error("Closing connection due to exception: ", cause);
            ctx.close();
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }
    }
}
