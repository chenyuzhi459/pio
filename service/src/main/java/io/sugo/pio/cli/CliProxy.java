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
import io.sugo.pio.recommend.RecommendManager;
import io.sugo.pio.recommend.manage.http.RecManageResource;
import io.sugo.pio.server.initialization.jetty.JettyServerInitializer;
import org.eclipse.jetty.server.Server;

import java.util.List;

/**
 */
@Command(
        name = "proxy",
        description = "Runs a pio proxy"
)
public class CliProxy extends ServerRunnable {
    private static final Logger log = new Logger(CliProxy.class);

    public CliProxy() {
        super(log);
    }

    @Override
    protected List<? extends Module> getModules() {
        return ImmutableList.<Module>of(
                new Module() {
                    @Override
                    public void configure(Binder binder) {
                        binder.bindConstant().annotatedWith(Names.named(CliConst.SERVICE_NAME)).to(CliConst.PROXY_NAME);
                        binder.bindConstant().annotatedWith(Names.named(CliConst.SERVICE_PORT)).to(CliConst.PROXY_PORT);
                        LifecycleModule.register(binder, RecommendManager.class);

                        Jerseys.addResource(binder, RecManageResource.class);

                        binder.bind(JettyServerInitializer.class).to(UIJettyServerInitializer.class).in(LazySingleton.class);
                        LifecycleModule.register(binder, Server.class);
                    }
                });
    }

}
