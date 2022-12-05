package org.mnikitin.lesson6.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.mnikitin.lesson6.service.SimpleProtocolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleProtocolHandler extends SimpleChannelInboundHandler<String> {
    private static final Logger log = LoggerFactory.getLogger(SimpleProtocolHandler.class);

    private final SimpleProtocolService simpleProtocolService;

    public SimpleProtocolHandler(SimpleProtocolService simpleProtocolService) {
        this.simpleProtocolService = simpleProtocolService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("Connected client: {}", ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) {
        var response = simpleProtocolService.processMessage(s);
        log.info("Sending response [{}] to client {}", response, ctx);
        ctx.writeAndFlush(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Closing connection for client {} due to exception: {}", ctx, cause);
        ctx.close();
    }
}
