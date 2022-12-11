package org.mnikitin.lesson7;

import org.assertj.core.api.Assertions;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.dynamic.HttpClientTransportDynamic;
import org.eclipse.jetty.io.ClientConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;


public class JettyHTTPGreeterServerTest {

    private static JettyHTTPGreeterServer server;

    @BeforeAll
    static void init() throws Exception {
        server = new JettyHTTPGreeterServer();
        server.start();
    }

    @AfterAll
    static void clean() throws Exception {
        server.stop();
    }

    @Test
    public void whenNameSentByHTTPTo8080_thenHelloNameReceived() throws Exception {
        var httpClient = new HttpClient();
        httpClient.setFollowRedirects(false);
        httpClient.start();

        var response = httpClient.GET("http://127.0.0.1:8080/service?name=Maksim");
        validateReponse(response);

        httpClient.stop();
    }

    @Test
    public void whenNameSentByHTTPSTo8443_thenHelloNameReceived() throws Exception {
        var sslContextFactory = new SslContextFactory.Client();
        sslContextFactory.setTrustAll(true);
        sslContextFactory.setEndpointIdentificationAlgorithm(null);

        var clientConnector = new ClientConnector();
        clientConnector.setSslContextFactory(sslContextFactory);

        var httpClient = new HttpClient(new HttpClientTransportDynamic(clientConnector));
        httpClient.setFollowRedirects(false);

        httpClient.start();

        var response = httpClient.GET("https://127.0.0.1:8443/service?name=Maksim");
        validateReponse(response);

        httpClient.stop();
    }

    private void validateReponse(ContentResponse response) {
        Assertions.assertThat(response.getStatus()).isEqualTo(200);
        Assertions.assertThat(response.getMediaType()).isEqualTo("text/plain");
        Assertions.assertThat(new String(response.getContent(), StandardCharsets.UTF_8))
                .isEqualTo("Hello Maksim");
    }
}
