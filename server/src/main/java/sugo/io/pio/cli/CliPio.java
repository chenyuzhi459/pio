package sugo.io.pio.cli;

import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Module;
import io.airlift.airline.Command;
import org.eclipse.jetty.server.Server;
import sugo.io.pio.guice.Jerseys;
import sugo.io.pio.guice.LazySingleton;
import sugo.io.pio.guice.LifecycleModule;
import sugo.io.pio.initialization.jetty.JettyServerInitializer;
import sugo.io.pio.metadata.SQLMetadataEngineStorage;
import sugo.io.pio.server.EngineResource;
import sugo.io.pio.server.EngineStorage;
import sugo.io.pio.server.TaskResource;
import sugo.io.pio.services.ServerRunnable;

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

                        binder.bind(JettyServerInitializer.class).to(UIJettyServerInitializer.class).in(LazySingleton.class);
                        LifecycleModule.register(binder, Server.class);

                        binder.bind(EngineStorage.class).to(SQLMetadataEngineStorage.class).in(LazySingleton.class);
                        LifecycleModule.register(binder, SQLMetadataEngineStorage.class);
                    }
                });
    }

}
