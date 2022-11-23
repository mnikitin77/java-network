package org.mnikitin.lesson5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class NIOClient {
    private static final int SERVER_PORT = 8888;
    private static final int BUFFER_SIZE = 8192;
    private static final Logger log = LoggerFactory.getLogger(NIOClient.class);

    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            log.error("Please, provide the host");
        } else {
            var address = InetAddress.getByName(args[0]);
            var buffer = ByteBuffer.allocate(BUFFER_SIZE);

            try (var socketChannel = SocketChannel.open(new InetSocketAddress(address, SERVER_PORT));) {

                buffer = ByteBuffer.wrap("Hello".getBytes());
                socketChannel.write(buffer);
                log.info("The word \"Hello\" sent to {}:{}", address, SERVER_PORT);
                buffer.clear();

                socketChannel.read(buffer);
                var response = new String(buffer.array(), 0, buffer.position(), StandardCharsets.UTF_8)
                        .trim();
                log.info("Received response from the server: {}", response);
                buffer.clear();

                buffer.put("Stop".getBytes());
                buffer.flip();
                socketChannel.write(buffer);
            }
        }
    }
}
