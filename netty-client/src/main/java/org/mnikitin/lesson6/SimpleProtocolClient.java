package org.mnikitin.lesson6;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleProtocolClient {

    private static final Logger log = LoggerFactory.getLogger(SimpleProtocolClient.class);
    private SocketChannel channel;
    private String response;
    private ChannelPromise requestPromise;


    public SimpleProtocolClient(int port, String host) throws InterruptedException {

        var t = new Thread(() -> {
            var workerGroup = new NioEventLoopGroup();
            try {
                var b = new Bootstrap();
                b.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) {
                                channel = ch;
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
        });

        t.setDaemon(true);
        t.start();

// Не знаю, как синхронизировать эти два потока между собой грамотно.
// Если делать всё в одном потоке, то блокируется на строке 41 f.channel().closeFuture().sync();
        Thread.sleep(5000);
    }

    public String sendMessage(String msg) {
        response = null;
        requestPromise = channel.newPromise();
        channel.writeAndFlush(msg);
// В строке 62 блокируется навсегда. Если вызывать с таймаутом, то по истечению исключение.
        requestPromise.awaitUninterruptibly();
        return response;
    }

    public void close() {
        channel.close();
    }

    public class ClientHandler extends SimpleChannelInboundHandler<String> {

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) {
            response = s;
            log.info("Received response from server: {}", response);
            requestPromise.setSuccess();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.error("Closing connection due to exception: ", cause);
            ctx.close();
        }
    }
}
