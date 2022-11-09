package lesson3.service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lesson3.exception.InvalidLoginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

public class HttpRequestHandlerService implements HttpHandler {

    private static final String LOGIN_PAYLOAD_KEY = "login";
    private static final String AUTHORIZED_LOGIN_VALUE = "java";

    private static final Logger log = LoggerFactory.getLogger(HttpRequestHandlerService.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "POST":
                    handlePostRequest(exchange);
                    break;
                default:
                    log.warn("Unsupported HTTP method: {}", exchange.getRequestMethod());
            }
        } catch (InvalidLoginException e) {
            log.error("Request for {} is bad",
                    exchange.getRequestURI()
            );
            try (final var responseBody = exchange.getResponseBody()) {
                exchange.sendResponseHeaders(400, e.getMessage().length());
                responseBody.write(e.getMessage().getBytes(StandardCharsets.UTF_8));
                responseBody.flush();
            }
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        var body = resquestBodyToString(exchange);

        var payload = new HashMap<String, String>();
        Arrays.stream(body.split("&")).forEach( it -> {
            String[] pair = it.split("=", 2);
            payload.put(pair[0], pair[1]);
        });

        if (AUTHORIZED_LOGIN_VALUE.equals(payload.get(LOGIN_PAYLOAD_KEY)) ) {
            try (final var responseBody = exchange.getResponseBody()) {
                String response = "Hello Java";
                exchange.sendResponseHeaders(200, response.length());
                responseBody.write("Hello Java".getBytes(StandardCharsets.UTF_8));
                responseBody.flush();
            }
        } else {
            throw new InvalidLoginException("Invalid request login: " + payload.get(LOGIN_PAYLOAD_KEY));
        }
    }

    private String resquestBodyToString(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }
}
