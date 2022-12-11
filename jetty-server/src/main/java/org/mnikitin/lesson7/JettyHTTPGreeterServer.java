package org.mnikitin.lesson7;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class JettyHTTPGreeterServer {

    private final static int HTTP_PORT = 8080;
    private final static int HTTPS_PORT = 8443;
    private static final Logger log = LoggerFactory.getLogger(JettyHTTPGreeterServer.class);

    private Server server;

    public static void main(String[] args) throws Exception {
        new JettyHTTPGreeterServer().start();
    }

    public void start() throws Exception {
        log.info("Starting JettyHTTPGreeterServer ...");

        var server = new Server();

        configureHttp(server);
        configureHttps(server);

        var servletHandler = new ServletHandler();
        server.setHandler(servletHandler);
        servletHandler.addServletWithMapping(GreeterServlet.class, "/service");
        server.start();
    }

    public void stop() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    private void configureHttp(Server server) {
        try (var connector = new ServerConnector(server)) {

            var http = new HttpConfiguration();
            http.addCustomizer(new SecureRequestCustomizer());
            // Configuration for HTTPS redirect
            http.setSecurePort(HTTPS_PORT);
            http.setSecureScheme("https");

            connector.addConnectionFactory(new HttpConnectionFactory(http));
            connector.setPort(HTTP_PORT);
            server.addConnector(connector);
        }
    }

    private void configureHttps(Server server) {
        var https = new HttpConfiguration();
        var customizer = new SecureRequestCustomizer();
        customizer.setSniHostCheck(false);
        https.addCustomizer(customizer);

        var pathToKeystore = Objects.requireNonNull(JettyHTTPGreeterServer.class.getResource("/testkeystore")).toExternalForm();
        var sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(pathToKeystore);
        sslContextFactory.setKeyStorePassword("qwerty");

        var connectionFactory = new HttpConnectionFactory(https);
        var tls = new SslConnectionFactory(sslContextFactory, connectionFactory.getProtocol());
        try (var connector = new ServerConnector(server, tls, connectionFactory)) {
            connector.setPort(HTTPS_PORT);
            server.addConnector(connector);
        }
    }
}
