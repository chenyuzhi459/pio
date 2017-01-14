package io.sugo.pio.cli;

import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.metamx.common.logger.Logger;
import io.airlift.airline.Command;
import io.sugo.pio.common.config.TaskConfig;
import io.sugo.pio.guice.*;
import io.sugo.pio.overlord.ForkingTaskRunner;
import io.sugo.pio.overlord.TaskRunner;
import io.sugo.pio.server.PioNode;
import io.sugo.pio.server.initialization.jetty.JettyServerInitializer;
import io.sugo.pio.worker.Worker;
import io.sugo.pio.worker.WorkerCuratorCoordinator;
import io.sugo.pio.worker.WorkerTaskMonitor;
import io.sugo.pio.worker.config.WorkerConfig;
import io.sugo.pio.worker.http.WorkerResource;
import org.eclipse.jetty.server.Server;

import java.util.List;

@Command(
        name = "middleManager",
        description = "Runs a Middle Manager, this is a \"task\" node used as part of the remote indexing service."
)
public class CliMiddleManager extends ServerRunnable {
    private static final Logger log = new Logger(CliMiddleManager.class);

    public CliMiddleManager()
    {
        super(log);
    }


    @Override
    protected List<? extends Module> getModules() {
        return ImmutableList.<Module>of(
                new Module() {
                    @Override
                    public void configure(Binder binder) {
                        binder.bindConstant().annotatedWith(Names.named("serviceName")).to("pio/middlemanager");
                        binder.bindConstant().annotatedWith(Names.named("servicePort")).to(8091);

                        TaskServiceModuleHelper.configureTaskRunnerConfigs(binder);

                        JsonConfigProvider.bind(binder, "pio.task", TaskConfig.class);
                        JsonConfigProvider.bind(binder, "pio.worker", WorkerConfig.class);

                        binder.bind(TaskRunner.class).to(ForkingTaskRunner.class);
                        binder.bind(ForkingTaskRunner.class).in(LazySingleton.class);

                        binder.bind(WorkerTaskMonitor.class).in(ManageLifecycle.class);
                        binder.bind(WorkerCuratorCoordinator.class).in(ManageLifecycle.class);

                        LifecycleModule.register(binder, WorkerTaskMonitor.class);
                        binder.bind(JettyServerInitializer.class).to(MiddleManagerJettyServerInitializer.class).in(LazySingleton.class);
                        Jerseys.addResource(binder, WorkerResource.class);

                        LifecycleModule.register(binder, Server.class);
                    }

                    @Provides
                    @LazySingleton
                    public Worker getWorker(@Self PioNode node, WorkerConfig config)
                    {
                        return new Worker(
                                node.getHostAndPort(),
                                config.getIp(),
                                config.getCapacity(),
                                config.getVersion()
                        );
                    }
                });
    }
}
