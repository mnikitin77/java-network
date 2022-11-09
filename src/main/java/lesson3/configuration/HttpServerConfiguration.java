package lesson3.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

public class HttpServerConfiguration {

    private static final String FILE_NAME = "server.properties";

    private final String host;
    private final int port;
    private final int numberOfThreads;

    public HttpServerConfiguration() throws IOException {
        String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        Properties props = new Properties();
        props.load(new FileInputStream(rootPath + FILE_NAME));

        host = Optional.ofNullable(props.getProperty("host")).orElse("localhost");
        port = Integer.parseInt(Optional.ofNullable(props.getProperty("port")).orElse("8080"));
        numberOfThreads = Integer.parseInt(Optional.ofNullable(props.getProperty("threads"))
                .orElseGet(() -> String.valueOf(Runtime.getRuntime().availableProcessors() * 2 + 1)));
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public int numberOfThreads() {
        return numberOfThreads;
    }
}
