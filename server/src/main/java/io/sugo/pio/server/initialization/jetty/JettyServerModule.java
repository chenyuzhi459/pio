package io.sugo.pio.server.initialization.jetty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;
import com.google.inject.*;
import com.google.inject.multibindings.Multibinder;
import com.metamx.common.lifecycle.Lifecycle;
import com.metamx.common.logger.Logger;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;
import io.sugo.pio.guice.annotations.JSR311Resource;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import io.sugo.pio.guice.JsonConfigProvider;
import io.sugo.pio.guice.LazySingleton;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.initialization.ServerConfig;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


/**
 */
public class JettyServerModule extends JerseyServletModule
{
    private static final Logger log = new Logger(JettyServerModule.class);

    private static final AtomicInteger activeConnections = new AtomicInteger();

    @Override
    protected void configureServlets()
    {
        Binder binder = binder();

        JsonConfigProvider.bind(binder, "pio.server.http", ServerConfig.class);

        binder.bind(GuiceContainer.class).to(PioGuiceContainer.class);
        binder.bind(PioGuiceContainer.class).in(Scopes.SINGLETON);
        binder.bind(CustomExceptionMapper.class).in(Singleton.class);

        serve("/*").with(PioGuiceContainer.class);

        //Adding empty binding for ServletFilterHolders so that injector returns
        //an empty set when no external modules provide ServletFilterHolder impls
        Multibinder.newSetBinder(binder, ServletFilterHolder.class);
    }

    public static class PioGuiceContainer extends GuiceContainer
    {
        private final Set<Class<?>> resources;

        @Inject
        public PioGuiceContainer(
                Injector injector,
                @JSR311Resource Set<Class<?>> resources
        )
        {
            super(injector);
            this.resources = resources;
        }

        @Override
        protected ResourceConfig getDefaultResourceConfig(
                Map<String, Object> props, WebConfig webConfig
        ) throws ServletException
        {
            return new DefaultResourceConfig(resources);
        }
    }

    @Provides
    @LazySingleton
    public Server getServer(
            final Injector injector, final Lifecycle lifecycle, final ServerConfig config
    )
    {
        final Server server = makeJettyServer(config);
        initializeServer(injector, lifecycle, server);
        return server;
    }

    @Provides
    @Singleton
    public JacksonJsonProvider getJacksonJsonProvider(@Json ObjectMapper objectMapper)
    {
        final JacksonJsonProvider provider = new JacksonJsonProvider();
        provider.setMapper(objectMapper);
        return provider;
    }

    static Server makeJettyServer(ServerConfig config)
    {
        final QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMinThreads(config.getNumThreads());
        threadPool.setMaxThreads(config.getNumThreads());
        threadPool.setDaemon(true);

        final Server server = new Server(threadPool);

        // Without this bean set, the default ScheduledExecutorScheduler runs as non-daemon, causing lifecycle hooks to fail
        // to fire on main exit. Related bug: https://github.com/druid-io/druid/pull/1627
        server.addBean(new ScheduledExecutorScheduler("JettyScheduler", true), true);

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(config.getPort());
        connector.setIdleTimeout(Ints.checkedCast(config.getMaxIdleTime().toStandardDuration().getMillis()));
        // workaround suggested in -
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=435322#c66 for jetty half open connection issues during failovers
        connector.setAcceptorPriorityDelta(-1);

        List<ConnectionFactory> monitoredConnFactories = new ArrayList<>();
        for (ConnectionFactory cf : connector.getConnectionFactories()) {
            monitoredConnFactories.add(new JettyMonitoringConnectionFactory(cf, activeConnections));
        }
        connector.setConnectionFactories(monitoredConnFactories);

        server.setConnectors(new Connector[]{connector});

        return server;
    }

    static void initializeServer(Injector injector, Lifecycle lifecycle, final Server server)
    {
        JettyServerInitializer initializer = injector.getInstance(JettyServerInitializer.class);
        try {
            initializer.initialize(server, injector);
        }
        catch (ConfigurationException e) {
            throw new ProvisionException(Iterables.getFirst(e.getErrorMessages(), null).getMessage());
        }

        lifecycle.addHandler(
                new Lifecycle.Handler()
                {
                    @Override
                    public void start() throws Exception
                    {
                        server.start();
                    }

                    @Override
                    public void stop()
                    {
                        try {
                            server.stop();
                        }
                        catch (Exception e) {
                            log.warn(e, "Unable to stop Jetty server.");
                        }
                    }
                }
        );
    }
}
