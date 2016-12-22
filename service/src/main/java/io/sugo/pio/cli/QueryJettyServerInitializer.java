package io.sugo.pio.cli;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import io.sugo.pio.server.initialization.jetty.JettyServerInitUtils;
import io.sugo.pio.server.initialization.jetty.JettyServerInitializer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 */
public class QueryJettyServerInitializer implements JettyServerInitializer {
    @Override
    public void initialize(Server server, Injector injector) {
        final ServletContextHandler root = new ServletContextHandler(ServletContextHandler.SESSIONS);
        root.addServlet(new ServletHolder(new DefaultServlet()), "/*");
        JettyServerInitUtils.addExtensionFilters(root, injector);
        root.addFilter(JettyServerInitUtils.defaultGzipFilterHolder(), "/*", null);

        root.addFilter(GuiceFilter.class, "/*", null);

        final HandlerList handlerList = new HandlerList();
        handlerList.setHandlers(new Handler[]{JettyServerInitUtils.getJettyRequestLogHandler(), root});
        server.setHandler(handlerList);
    }
}
