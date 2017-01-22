package io.sugo.pio.engine.demo;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import io.sugo.pio.engine.demo.http.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.ResourceCollection;

import java.io.IOException;

/**
 */
public class DemoServer {
    public static void main(String[] args) throws Exception {
//        train();
        startServer();
    }

    private static void train() throws IOException {
        PopularTraining popularTraining = new PopularTraining();
        popularTraining.train();
//        ALSTraining alsTraining = new ALSTraining();
//        alsTraining.train();
//        DetailTraining detailTraining = new DetailTraining();
//        detailTraining.train();
//        FpTraining fpTraining = new FpTraining();
//        fpTraining.train();
        SearchTraining searchTraining = new SearchTraining();
        searchTraining.train();
//        UserHistoryTraining userHistoryTraining = new UserHistoryTraining();
//        userHistoryTraining.train();
    }

    private static void startServer() throws Exception {
        Server server = new Server(8080);
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "true");
        ServletHolder servlet = new ServletHolder(ServletContainer.class);
        servlet.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");
        servlet.setInitParameter("com.sun.jersey.config.property.packages", "io.sugo.pio.engine.demo.http");
        handler.setWelcomeFiles(new String[]{"index.html"});

        ServletHolder holderPwd = new ServletHolder("default", DefaultServlet.class);

        handler.addServlet(holderPwd, "/");
        handler.addServlet(servlet, "/pio/*");
        handler.setBaseResource(
                new ResourceCollection(
                        new String[]{
                                handler.getClass().getClassLoader().getResource("static").toExternalForm(),
                        }
                )
        );
        handler.setContextPath("/");
        server.setHandler(handler);
        server.start();
        server.join();
    }
}
