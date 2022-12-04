package org.mnikitin.lesson6;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleProtocolClientApp {

    private static final Logger log = LoggerFactory.getLogger(SimpleProtocolClientApp.class);

    public static void main(String[] args) throws InterruptedException {

        var host = "127.0.0.1";
        var port = 12345;

        var client = new SimpleProtocolClient(port, host);

        var res1 = client.sendMessage("5:hello");
        log.info("response1: {}", res1);
        var res2 = client.sendMessage("4:cool");
        log.info("response2: {}", res2);
        var res3 = client.sendMessage("6:haha");
        log.info("response3: {}", res3);

        client.close();
    }
}
