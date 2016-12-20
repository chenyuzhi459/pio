package io.sugo.pio.cli;

import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Module;
import io.airlift.airline.Command;
import io.sugo.pio.guice.Jerseys;
import io.sugo.pio.metadata.SQLMetadataEngineStorage;
import io.sugo.pio.server.EngineResource;
import io.sugo.pio.server.EngineStorage;
import io.sugo.pio.server.initialization.jetty.JettyServerInitializer;
import org.eclipse.jetty.server.Server;
import io.sugo.pio.guice.LazySingleton;
import io.sugo.pio.guice.LifecycleModule;
import io.sugo.pio.http.TaskResource;
import io.sugo.pio.services.ServerRunnable;

import java.util.List;

/**
 */
@Command(
        name = "pio",
        description = "Runs a pio server"
)
public class CliPio extends ServerRunnable {
    @Override
    protected List<? extends Module> getModules() {
        return ImmutableList.<Module>of(
                new Module() {
                    @Override
                    public void configure(Binder binder) {
                        Jerseys.addResource(binder, EngineResource.class);
                        Jerseys.addResource(binder, TaskResource.class);

                        binder.bind(JettyServerInitializer.class).to(QueryJettyServerInitializer.class).in(LazySingleton.class);
                        LifecycleModule.register(binder, Server.class);

                        binder.bind(EngineStorage.class).to(SQLMetadataEngineStorage.class).in(LazySingleton.class);
                        LifecycleModule.register(binder, SQLMetadataEngineStorage.class);
                    }
                });
    }

}
