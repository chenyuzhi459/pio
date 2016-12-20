package io.sugo.pio.cli;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.ResourceCollection;
import io.sugo.pio.server.initialization.jetty.JettyServerInitUtils;
import io.sugo.pio.server.initialization.jetty.JettyServerInitializer;

/**
 */
public class UIJettyServerInitializer implements JettyServerInitializer {
    @Override
    public void initialize(Server server, Injector injector) {
        final ServletContextHandler root = new ServletContextHandler(ServletContextHandler.SESSIONS);
        root.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        root.setWelcomeFiles(new String[]{"index.html"});

        ServletHolder holderPwd = new ServletHolder("default", DefaultServlet.class);

        root.addServlet(holderPwd, "/");
        root.setBaseResource(
                new ResourceCollection(
                        new String[]{
                                this.getClass().getClassLoader().getResource("static").toExternalForm(),
                        }
                )
        );

        JettyServerInitUtils.addExtensionFilters(root, injector);
        root.addFilter(JettyServerInitUtils.defaultGzipFilterHolder(), "/*", null);

        root.addFilter(GuiceFilter.class, "/pio/*", null);

        HandlerList handlerList = new HandlerList();
        handlerList.setHandlers(new Handler[]{JettyServerInitUtils.getJettyRequestLogHandler(), root});

        server.setHandler(handlerList);
    }
}
