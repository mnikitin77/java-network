package lesson3.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpServerClientExample {

    private static final String URI = "http://localhost:8089/greeter";
    private static final Logger log = LoggerFactory.getLogger(HttpServerClientExample.class);

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient
                .newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(new URI(URI))
                .POST(HttpRequest.BodyPublishers.ofString("login=java&password=qwerty"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    log.info("Reqest for {} resulted with code {}", URI, response.statusCode());
                    log.info("Response URI: {}", response.uri().toString());
                    log.info("Response body: {}", response.body());
                }).join();
    }
}
