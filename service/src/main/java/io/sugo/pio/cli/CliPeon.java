package io.sugo.pio.cli;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.metamx.common.lifecycle.Lifecycle;
import com.metamx.common.logger.Logger;
import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.Option;
import io.sugo.pio.common.TaskToolboxFactory;
import io.sugo.pio.common.config.TaskConfig;
import io.sugo.pio.guice.*;
import io.sugo.pio.overlord.TaskRunner;
import io.sugo.pio.overlord.ThreadPoolTaskRunner;
import io.sugo.pio.query.QueryWalker;
import io.sugo.pio.server.QueryResource;
import io.sugo.pio.server.initialization.jetty.JettyServerInitializer;
import io.sugo.pio.worker.executor.ExecutorLifecycle;
import io.sugo.pio.worker.executor.ExecutorLifecycleConfig;
import org.eclipse.jetty.server.Server;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 */
@Command(
        name = "peon",
        description = "Runs a Peon, this is an individual forked \"task\" used as part of the indexing service. "
                + "This should rarely, if ever, be used directly."
)
public class CliPeon extends GuiceRunnable {
    @Arguments(description = "task.json status.json", required = true)
    public List<String> taskAndStatusFile;

    @Option(name = "--nodeType", title = "nodeType", description = "Set the node type to expose on ZK")
    public String nodeType = "executor";


    private static final Logger log = new Logger(CliPeon.class);

    @Inject
    private Properties properties;

    public CliPeon()
    {
        super(log);
    }

    @Override
    protected List<? extends Module> getModules() {
        return ImmutableList.<Module>of(
                new Module() {
                    @Override
                    public void configure(Binder binder) {
                        binder.bindConstant().annotatedWith(Names.named("serviceName")).to("pio/peon");
                        binder.bindConstant().annotatedWith(Names.named("servicePort")).to(-1);

                        binder.bind(TaskToolboxFactory.class).in(LazySingleton.class);

                        JsonConfigProvider.bind(binder, "pio.task", TaskConfig.class);

                        binder.bind(ExecutorLifecycle.class).in(ManageLifecycle.class);
                        LifecycleModule.register(binder, ExecutorLifecycle.class);
                        binder.bind(ExecutorLifecycleConfig.class).toInstance(
                                new ExecutorLifecycleConfig()
                                        .setTaskFile(new File(taskAndStatusFile.get(0)))
                                        .setStatusFile(new File(taskAndStatusFile.get(1)))
                        );

                        binder.bind(TaskRunner.class).to(ThreadPoolTaskRunner.class);
                        binder.bind(QueryWalker.class).to(ThreadPoolTaskRunner.class);
                        binder.bind(ThreadPoolTaskRunner.class).in(ManageLifecycle.class);

                        binder.bind(JettyServerInitializer.class).to(QueryJettyServerInitializer.class);
                        Jerseys.addResource(binder, QueryResource.class);
                        LifecycleModule.register(binder, QueryResource.class);
                        binder.bind(NodeTypeConfig.class).toInstance(new NodeTypeConfig(nodeType));
                        LifecycleModule.register(binder, Server.class);
                    }
                });
    }

    @Override
    public void run() {
        try {
            Injector injector = makeInjector();
            try {
                final Lifecycle lifecycle = initLifecycle(injector);
                final Thread hook = new Thread(
                        new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                lifecycle.stop();
                            }
                        }
                );
                Runtime.getRuntime().addShutdownHook(hook);
                injector.getInstance(ExecutorLifecycle.class).join();
                // Explicitly call lifecycle stop, dont rely on shutdown hook.
                lifecycle.stop();
                Runtime.getRuntime().removeShutdownHook(hook);
            }
            catch (Throwable t) {
                System.exit(1);
            }
        }
        catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
}
