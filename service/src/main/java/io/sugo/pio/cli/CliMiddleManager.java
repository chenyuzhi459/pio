package io.sugo.pio.cli;

import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Module;
import io.airlift.airline.Command;
import io.sugo.pio.guice.Jerseys;
import io.sugo.pio.guice.LazySingleton;
import io.sugo.pio.guice.LifecycleModule;
import io.sugo.pio.http.TaskResource;
import io.sugo.pio.server.initialization.jetty.JettyServerInitializer;
import io.sugo.pio.services.ServerRunnable;
import org.eclipse.jetty.server.Server;

import java.util.List;

@Command(
        name = "middleManager",
        description = "Runs a Middle Manager, this is a \"task\" node used as part of the remote indexing service."
)
public class CliMiddleManager extends ServerRunnable {
    @Override
    protected List<? extends Module> getModules() {
        return ImmutableList.<Module>of(
                new Module() {
                    @Override
                    public void configure(Binder binder) {
                        binder.bind(JettyServerInitializer.class).to(QueryJettyServerInitializer.class).in(LazySingleton.class);
                        LifecycleModule.register(binder, Server.class);
                    }
                });
    }
}
