package lesson3;

import com.sun.net.httpserver.HttpServer;
import lesson3.configuration.HttpServerConfiguration;
import lesson3.service.HttpRequestHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class LiteHttpServer {

    private static final int SERVER_SHUTDOWN_DELAY = 1;
    private static final Logger log = LoggerFactory.getLogger(LiteHttpServer.class);

    private HttpServer server;

    public static void main(String[] args) throws IOException {
        new LiteHttpServer().start();
    }

    public void start() throws IOException {
        final HttpServerConfiguration config = new HttpServerConfiguration();
        log.info("Starting server...");

        server = HttpServer.create(new InetSocketAddress(config.host(), config.port()), 0);
        server.setExecutor(Executors.newFixedThreadPool(config.numberOfThreads()));
        server.createContext("/greeter", new HttpRequestHandlerService());
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(SERVER_SHUTDOWN_DELAY);
        }
    }
}
