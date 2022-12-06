package org.mnikitin.lesson6;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SimpleProtocolClientTest {

    @BeforeAll
    static void init(){
        new Thread(() -> {
            try {
                new SimpleProtocolServer(12345);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Test
    void whenSendToSimpleClientServer_ItWorksWell() throws InterruptedException {
        var host = "127.0.0.1";
        var port = 12345;
        var client = new SimpleProtocolClient(port, host);
        client.start();


        String response = client.sendRequest("5:hello");
        Assertions.assertThat(response).isEqualTo("2:ok");

        response = client.sendRequest("4:cool");
        Assertions.assertThat(response).isEqualTo("2:ok");

        response = client.sendRequest("6:haha");
        Assertions.assertThat(response).isEqualTo("3:err");

        client.stop();
    }
}
