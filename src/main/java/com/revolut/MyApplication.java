package com.revolut;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpServer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.servlet.ServletContainer;

public class MyApplication {

    public static void main(String[] args) throws IOException {
        startServer(8081, true);
    }

    public static Server startServer(int port, boolean joinThread) {
        Server server = new Server(port);
        ServletContextHandler ctx =
                new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

        ctx.setContextPath("/");
        server.setHandler(ctx);

        ServletHolder serHol = ctx.addServlet(ServletContainer.class, "/api/*");
        serHol.setInitOrder(1);
        serHol.setInitParameter("jersey.config.server.provider.packages",
                "com.revolut.controller");

        try {
            server.start();
            if (joinThread) {
                server.join();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (joinThread) {
                server.destroy();
            }
        }
        return server;
    }
}
