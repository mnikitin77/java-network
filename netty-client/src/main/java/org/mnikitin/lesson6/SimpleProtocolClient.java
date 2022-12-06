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

import java.util.concurrent.CountDownLatch;

public class SimpleProtocolClient {

    private static final Logger log = LoggerFactory.getLogger(SimpleProtocolClient.class);

    private final int port;
    private final String host;
    private Channel channel;
    private ChannelPromise promiseOfResponse;
    private String response;

    public SimpleProtocolClient(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public void start() throws InterruptedException {
        if (channel != null) {
            log.warn("SimpleProtocolClient is already on");
            return;
        }

        log.info("Starting SimpleProtocolClient ...");
        var clientStarted = new CountDownLatch(1);

//  This thread is required not to block the client's thread on channel.closeFuture().sync()
        var processingThread = new Thread(() -> {
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
                channel = f.channel();
                log.info("Channel is open");
                clientStarted.countDown();

                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                log.error("Error on starting SimpleProtocolClient");
            } finally {
                workerGroup.shutdownGracefully();
            }
        });

        processingThread.setDaemon(true);
        processingThread.start();

        clientStarted.await();
        log.info("SimpleProtocolClient started!");
    }

    public void stop() {
        if (channel != null && channel.isOpen()) {
            channel.close().addListener((ChannelFutureListener) l -> {
                        if (l.isDone()) {
                            log.info("SimpleProtocolClient stopped");
                        }
                    }
            );
        }
    }

    public String sendRequest(String message) {
        promiseOfResponse = channel.newPromise();
        channel.writeAndFlush(message);
        promiseOfResponse.awaitUninterruptibly();
        return response;
    }

    public class ClientHandler extends SimpleChannelInboundHandler<String> {

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            log.info("Channel is active");
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String s) {
            response = s;
            if (promiseOfResponse != null) {
                promiseOfResponse.setSuccess();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.error("Closing connection due to exception: ", cause);
            if (promiseOfResponse != null) {
                promiseOfResponse.setFailure(cause);
            }
            ctx.close();
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }
    }
}
