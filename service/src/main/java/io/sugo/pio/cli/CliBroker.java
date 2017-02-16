package io.sugo.pio.cli;

import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.metamx.common.logger.Logger;
import io.airlift.airline.Command;
import io.sugo.pio.client.BrokerServerView;
import io.sugo.pio.client.CachingClusteredClient;
import io.sugo.pio.client.selector.ServerSelectorStrategy;
import io.sugo.pio.client.selector.TierSelectorStrategy;
import io.sugo.pio.guice.Jerseys;
import io.sugo.pio.guice.JsonConfigProvider;
import io.sugo.pio.guice.LazySingleton;
import io.sugo.pio.guice.LifecycleModule;
import io.sugo.pio.query.QueryWalker;
import io.sugo.pio.server.ClientQueryWalker;
import io.sugo.pio.server.QueryResource;
import io.sugo.pio.server.initialization.jetty.JettyServerInitializer;
import org.eclipse.jetty.server.Server;

import java.util.List;

/**
 */
@Command(
        name = "broker",
        description = "Runs a broker node"
)
public class CliBroker extends ServerRunnable {
    private static final Logger log = new Logger(CliBroker.class);

    public CliBroker() {
        super(log);
    }

    @Override
    protected List<? extends Module> getModules() {
        return ImmutableList.of(
                new Module() {
                    @Override
                    public void configure(Binder binder) {
                        binder.bindConstant().annotatedWith(Names.named(CliConst.SERVICE_NAME)).to(CliConst.BROKER_NAME);
                        binder.bindConstant().annotatedWith(Names.named(CliConst.SERVICE_PORT)).to(CliConst.BROKER_PORT);

                        binder.bind(CachingClusteredClient.class).in(LazySingleton.class);
                        binder.bind(BrokerServerView.class).in(LazySingleton.class);
                        binder.bind(QueryWalker.class).to(ClientQueryWalker.class).in(LazySingleton.class);

                        JsonConfigProvider.bind(binder, "pio.broker.select", TierSelectorStrategy.class);
                        JsonConfigProvider.bind(binder, "pio.broker.balancer", ServerSelectorStrategy.class);

                        binder.bind(JettyServerInitializer.class).to(QueryJettyServerInitializer.class).in(LazySingleton.class);
                        Jerseys.addResource(binder, QueryResource.class);
                        LifecycleModule.register(binder, QueryResource.class);
                        LifecycleModule.register(binder, Server.class);
                    }
                });
    }
}
