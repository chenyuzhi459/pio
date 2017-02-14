package io.sugo.pio.engine.demo;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import io.airlift.airline.Command;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.ResourceCollection;

/**
 */
@Command(
        name = "demo-server",
        description = "Runs a demo-server"
)
public class DemoServer implements Runnable {
    @Override
    public void run() {
        try {
            startServer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
