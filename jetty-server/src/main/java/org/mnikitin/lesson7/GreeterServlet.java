package org.mnikitin.lesson7;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GreeterServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(GreeterServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var nameParameter = req.getParameter("name");
        log.info("Received request to [{}], name={}", req.getRequestURI(), nameParameter);
        if (nameParameter != null) {
            resp.setContentType("text/plain");
            resp.getWriter().print("Hello " + nameParameter);
            log.info("Sent response back");
        } else {
            log.warn("Request is incorrect, name=null");
        }
    }
}
