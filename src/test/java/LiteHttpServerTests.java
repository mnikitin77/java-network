import lesson3.LiteHttpServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LiteHttpServerTests {

    private static final String URI = "http://localhost:8089/greeter";

    @Test
    public void whenCorrectRequestBodyThen200Ok() throws IOException, URISyntaxException, InterruptedException {
        sendRequestAndValidateResponse(
                "login=java&password=qwerty",
                "Hello Java",
                200
        );
    }

    @Test
    public void whenIncorrectRequestBodyThen400BadRequest() throws IOException, URISyntaxException, InterruptedException {
        sendRequestAndValidateResponse(
                "login=python&password=qwerty",
                "Invalid request login: python",
                400
        );
    }

    private void sendRequestAndValidateResponse(
            String body,
            String expectedResponseBody,
            int expectedCode
    ) throws IOException, URISyntaxException, InterruptedException {
        LiteHttpServer server = new LiteHttpServer();
        server.start();

        HttpClient client = HttpClient
                .newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(new URI(URI))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    Assertions.assertThat(response.statusCode()).isEqualTo(expectedCode);
                    Assertions.assertThat(response.uri().toString()).isEqualTo(URI);
                    Assertions.assertThat(response.body()).isEqualTo(expectedResponseBody);
                }).join();

        server.stop();
    }
}
