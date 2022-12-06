package org.mnikitin.lesson6;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleProtocolClientApp {

    private static final Logger log = LoggerFactory.getLogger(SimpleProtocolClientApp.class);

    public static void main(String[] args) throws InterruptedException {
        var host = "127.0.0.1";
        var port = 12345;
        var client = new SimpleProtocolClient(port, host);
        client.start();

        log.info("Sending message 1 [5:hello] to the server");
        String response = client.sendRequest("5:hello");
        log.info("Response on message 1: {}", response);

        log.info("Sending message 2 [4:cool] to the server");
        response = client.sendRequest("4:cool");
        log.info("Response on message 2: {}", response);

        log.info("Sending message 3 [6:haha] to the server");
        response = client.sendRequest("6:haha");
        log.info("Response on message 3: {}", response);

        client.stop();
    }
}
