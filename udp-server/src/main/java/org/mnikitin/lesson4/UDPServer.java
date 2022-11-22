package org.mnikitin.lesson4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class UDPServer {

    private static final Logger log = LoggerFactory.getLogger(UDPServer.class);

    private static final int PORT = 8888;
    private static final int BUFFER_SIZE = 8096;
    private static final String FINISH_CODE = "stop";

    public static void main(String[] args) throws IOException {

        var buffer = new byte[BUFFER_SIZE];
        var packet = new DatagramPacket(buffer, buffer.length);

        log.info("UDP Server started");

        try(var socket = new DatagramSocket(PORT);) {
            while (true) {
                socket.receive(packet);
                String payload = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                log.info("Received a packet from {}:{}\n\tpayload:{}",
                        packet.getAddress(), packet.getPort(), payload);

                if (FINISH_CODE.equalsIgnoreCase(payload)) {
                    break;
                }

                socket.send(
                        new DatagramPacket(
                                buffer,
                                buffer.length,
                                packet.getAddress(),
                                packet.getPort()
                        )
                );
            }
        }

        log.info("UDP Server stopped");
    }
}
