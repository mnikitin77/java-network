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

public class SimpleProtocolClientOld {

    private static final Logger log = LoggerFactory.getLogger(SimpleProtocolClientOld.class);

//    private final Channel channel;
    private Channel channel;
    private ChannelPromise requestPromise;
    private String response;

    private EventLoopGroup group = new NioEventLoopGroup(16);
    private Bootstrap bootstrap = new Bootstrap();

    private String host;
    private int port;

//    public SimpleProtocolClient() throws InterruptedException {
//        this(PORT, HOST);
//    }

    public SimpleProtocolClientOld(int port, String host) throws InterruptedException {
        this.host = host;
        this.port = port;

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addLast(new StringDecoder());
                p.addLast(new StringEncoder());
                p.addLast(new ClientHandler());
            }
        });

//        ChannelFuture f = bootstrap.connect(host, port).sync();
//        channel = f.sync().channel();
//        f.channel().closeFuture().sync();

//        new Thread(() -> {
//            EventLoopGroup group = new NioEventLoopGroup();
//            try {
//                var b = new Bootstrap();
//                b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
//                    @Override
//                    public void initChannel(SocketChannel ch) throws Exception {
//                        ChannelPipeline p = ch.pipeline();
//                        p.addLast(new StringDecoder());
//                        p.addLast(new StringEncoder());
//                        p.addLast(new ClientHandler());
//                    }
//                });
//
//                ChannelFuture f = b.connect(host, port).sync();
//                channel = f.sync().channel();
//                f.channel().closeFuture().sync();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            } finally {
//                group.shutdownGracefully();
//            }
//        }).start();

//        EventLoopGroup group = new NioEventLoopGroup();
//        try {
//            var b = new Bootstrap();
//            b.group(group)
//                    .channel(NioSocketChannel.class)
//                    .handler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        public void initChannel(SocketChannel ch) throws Exception {
//                            ChannelPipeline p = ch.pipeline();
//                            p.addLast(new StringDecoder());
//                            p.addLast(new StringEncoder());
//                            p.addLast(new  ClientHandler());
//                        }
//                    });
//
//            ChannelFuture f = b.connect(host, port).sync();
//            channel = f.sync().channel();
//            f.channel().closeFuture().sync();
//        } finally {
//            group.shutdownGracefully();
//        }
    }

    public void shutDown() {
//        Objects.requireNonNull(channel);
//        if(channel.isOpen()) {
//            channel.close();
//        }
        group.shutdownGracefully();
    }

    public void sendMessage(String message) throws InterruptedException {
       initRequest();
        channel.writeAndFlush(message);
//                .addListener((ChannelFutureListener) f -> {
//                    channel.close();
//                    log.info("Channel [{}] closed", channel);
//                });
    }

    public String sendMessageAndGetResponse(String message, long timeout) throws InterruptedException {
        sendMessage(message);
        channel.closeFuture().sync();
        return response;
//        if (requestPromise.awaitUninterruptibly(timeout)) {
//            return response;
//        }
//        return null;
    }

    class ClientHandler extends SimpleChannelInboundHandler<String> {

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            log.info("Client successfully connected: {}", ctx);
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) {
            response = msg;
            log.info("Received response from server: {}", response);
            requestPromise.setSuccess();
            ctx.close();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.error("Closing connection due to exception: ", cause);
            ctx.close();
        }
    }

//    private void initRequest() throws InterruptedException {
    private void initRequest() throws InterruptedException {
        var f = bootstrap.connect(host, port).sync();
        channel = f.sync().channel();
//        Objects.requireNonNull(channel);
        response = null;
        requestPromise = channel.newPromise();
    }
}
