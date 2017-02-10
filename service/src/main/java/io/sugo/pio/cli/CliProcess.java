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
import io.sugo.pio.server.http.OperatorResource;
import io.sugo.pio.server.http.ProcessResource;
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
    private static final Logger log = new Logger(CliProcess.class);

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
                        binder.bindConstant().annotatedWith(Names.named("servicePort")).to(6060);

                        Jerseys.addResource(binder, ProcessResource.class);
                        Jerseys.addResource(binder, OperatorResource.class);

                        binder.bind(JettyServerInitializer.class).to(UIJettyServerInitializer.class).in(LazySingleton.class);
                        LifecycleModule.register(binder, Server.class);
                    }
                });
    }

}
