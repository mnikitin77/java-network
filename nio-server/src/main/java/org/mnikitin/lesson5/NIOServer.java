package org.mnikitin.lesson5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class NIOServer {

    private static final Logger log = LoggerFactory.getLogger(NIOServer.class);

    private static final int PORT = 8888;
    private static final int BUFFER_SIZE = 8192;
    private static final String HOST = "localhost";
    private static final String FINISH_CODE = "stop";

    public static void main(String[] args) throws IOException {

        log.info("NIOServer started");

        try(var selector = Selector.open();
            var serverSocket = ServerSocketChannel.open();) {

            serverSocket.bind(new InetSocketAddress(HOST, PORT));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);

            var buffer = ByteBuffer.allocate(BUFFER_SIZE);
            var keepOnWorking = true;

            while (keepOnWorking) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while(iterator.hasNext()) {
                    var key = iterator.next();
                    if (key.isAcceptable()) {
                        accept(selector, serverSocket);
                    } else if (key.isReadable()) {
                        keepOnWorking = processRequest(buffer, key);
                    }
                    iterator.remove();
                }
            }
        }

        log.info("NIOServer stopped");
    }

    private static void accept(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        var client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

    private static boolean processRequest(ByteBuffer buffer, SelectionKey key) throws IOException {

        var client = (SocketChannel) key.channel();
        buffer.clear();
        client.read(buffer);

        if (buffer.hasArray()) {
            String payload = new String(buffer.array(), 0, buffer.position(), StandardCharsets.UTF_8)
                    .trim();
            log.info("Received data from {}\n\tpayload: {}", client.getRemoteAddress(), payload);

            if (FINISH_CODE.equalsIgnoreCase(payload)) {
                client.close();
                return false;
            }

            buffer.flip();
            client.write(buffer);
        }

        return true;
    }
}
