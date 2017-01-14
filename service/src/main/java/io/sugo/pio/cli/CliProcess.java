package io.sugo.pio.cli;

import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.metamx.common.logger.Logger;
import io.airlift.airline.Command;
import io.sugo.pio.guice.Jerseys;
import io.sugo.pio.guice.LazySingleton;
import io.sugo.pio.guice.LifecycleModule;
import io.sugo.pio.server.process.http.ProcessResource;
import io.sugo.pio.server.initialization.jetty.JettyServerInitializer;
import org.eclipse.jetty.server.Server;

import java.util.List;

/**
 */
@Command(
        name = "process",
        description = "Runs a pio server"
)
public class CliProcess extends ServerRunnable {
    private static final Logger log = new Logger(CliMiddleManager.class);

    public CliProcess()
    {
        super(log);
    }

    @Override
    protected List<? extends Module> getModules() {
        return ImmutableList.<Module>of(
                new Module() {
                    @Override
                    public void configure(Binder binder) {
                        binder.bindConstant()
                                .annotatedWith(Names.named("serviceName"))
                                .to("pio/process");
                        binder.bindConstant().annotatedWith(Names.named("servicePort")).to(8080);


                        Jerseys.addResource(binder, ProcessResource.class);

                        binder.bind(JettyServerInitializer.class).to(QueryJettyServerInitializer.class).in(LazySingleton.class);
                        LifecycleModule.register(binder, Server.class);
                    }
                });
    }

}
