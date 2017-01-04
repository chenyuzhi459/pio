package io.sugo.pio.cli;

import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Module;
import io.airlift.airline.Command;
import io.sugo.pio.guice.LifecycleModule;
import io.sugo.pio.server.initialization.jetty.JettyServerInitializer;
import io.sugo.pio.services.GuiceRunnable;
import org.eclipse.jetty.server.Server;

import java.util.List;

/**
 */
@Command(
        name = "peon",
        description = "Runs a Peon, this is an individual forked \"task\" used as part of the indexing service. "
                + "This should rarely, if ever, be used directly."
)
public class CliPeon extends GuiceRunnable {
    @Override
    protected List<? extends Module> getModules() {
        return ImmutableList.<Module>of(
                new Module() {
                    @Override
                    public void configure(Binder binder) {
                        binder.bind(JettyServerInitializer.class).to(QueryJettyServerInitializer.class);
                        LifecycleModule.register(binder, Server.class);
                    }
                });
    }

    @Override
    public void run() {

    }
}
