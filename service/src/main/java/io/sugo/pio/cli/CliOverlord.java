package io.sugo.pio.cli;

import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Module;
import io.airlift.airline.Command;
import io.sugo.pio.guice.Jerseys;
import io.sugo.pio.guice.LazySingleton;
import io.sugo.pio.guice.LifecycleModule;
import io.sugo.pio.overlord.http.OverlordResource;
import io.sugo.pio.server.initialization.jetty.JettyServerInitializer;
import org.eclipse.jetty.server.Server;

import java.util.List;

/**
 */
@Command(
        name = "overlord",
        description = "Runs an Overlord node, see http://druid.io/docs/latest/Indexing-Service.html for a description"
)
public class CliOverlord extends ServerRunnable {
    @Override
    protected List<? extends Module> getModules() {
        return ImmutableList.<Module>of(
                new Module() {
                    @Override
                    public void configure(Binder binder) {
                        Jerseys.addResource(binder, OverlordResource.class);

                        binder.bind(JettyServerInitializer.class).to(QueryJettyServerInitializer.class).in(LazySingleton.class);
                        LifecycleModule.register(binder, Server.class);
                    }
                });
    }
}
