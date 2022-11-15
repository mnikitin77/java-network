package org.mnikitin.lesson4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDPClient {

    private static final int SERVER_PORT = 8888;

    private static final Logger log = LoggerFactory.getLogger(UDPClient.class);

    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            log.error("Please, provide the host");
            System.exit(1);
        }

        var address = InetAddress.getByName(args[0]);
        try (var socket = new DatagramSocket();) {
            byte[] buffer = "Hello".getBytes();
            var packet = new DatagramPacket(buffer, buffer.length, address, SERVER_PORT);
            socket.send(packet);

            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            log.info("Received message from server: {}",
                    new String(packet.getData(), StandardCharsets.UTF_8));

            buffer = "Stop".getBytes();
            packet = new DatagramPacket(buffer, buffer.length, address, SERVER_PORT);
            socket.send(packet);
        }
    }
}
