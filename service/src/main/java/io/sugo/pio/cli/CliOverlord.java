package io.sugo.pio.cli;

import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceFilter;
import com.metamx.common.logger.Logger;
import io.airlift.airline.Command;
import io.sugo.pio.common.config.TaskConfig;
import io.sugo.pio.guice.*;
import io.sugo.pio.overlord.ForkingTaskRunnerFactory;
import io.sugo.pio.overlord.RemoteTaskRunnerFactory;
import io.sugo.pio.overlord.TaskMaster;
import io.sugo.pio.overlord.TaskRunnerFactory;
import io.sugo.pio.overlord.config.TaskQueueConfig;
import io.sugo.pio.overlord.http.OverlordRedirectInfo;
import io.sugo.pio.overlord.http.OverlordResource;
import io.sugo.pio.overlord.setup.WorkerBehaviorConfig;
import io.sugo.pio.server.http.RedirectFilter;
import io.sugo.pio.server.http.RedirectInfo;
import io.sugo.pio.server.initialization.jetty.JettyServerInitUtils;
import io.sugo.pio.server.initialization.jetty.JettyServerInitializer;
import io.sugo.pio.worker.config.WorkerConfig;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.ResourceCollection;

import java.util.List;

/**
 */
@Command(
        name = "overlord",
        description = "Runs an Overlord node"
)
public class CliOverlord extends ServerRunnable {
    private static Logger log = new Logger(CliOverlord.class);

    public CliOverlord()
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
                                .to("pio/overlord");
                        binder.bindConstant().annotatedWith(Names.named("servicePort")).to(8090);


                        JsonConfigProvider.bind(binder, "pio.task.queue", TaskQueueConfig.class);
                        JsonConfigProvider.bind(binder, "pio.task", TaskConfig.class);

                        binder.bind(TaskMaster.class).in(ManageLifecycle.class);

                        configureRunners(binder);
                        binder.bind(RedirectFilter.class).in(LazySingleton.class);
                        binder.bind(RedirectInfo.class).to(OverlordRedirectInfo.class).in(LazySingleton.class);

                        binder.bind(JettyServerInitializer.class).toInstance(new OverlordJettyServerInitializer());
                        Jerseys.addResource(binder, OverlordResource.class);

                        LifecycleModule.register(binder, Server.class);
                    }

                    private void configureRunners(Binder binder)
                    {
                        JsonConfigProvider.bind(binder, "pio.worker", WorkerConfig.class);


                        PolyBind.createChoice(
                                binder,
                                "pio.task.runner.type",
                                Key.get(TaskRunnerFactory.class),
                                Key.get(ForkingTaskRunnerFactory.class)
                        );
                        final MapBinder<String, TaskRunnerFactory> biddy = PolyBind.optionBinder(
                                binder,
                                Key.get(TaskRunnerFactory.class)
                        );

                        TaskServiceModuleHelper.configureTaskRunnerConfigs(binder);
                        biddy.addBinding("local").to(ForkingTaskRunnerFactory.class);
                        binder.bind(ForkingTaskRunnerFactory.class).in(LazySingleton.class);

                        biddy.addBinding(RemoteTaskRunnerFactory.TYPE_NAME).to(RemoteTaskRunnerFactory.class).in(LazySingleton.class);
                        binder.bind(RemoteTaskRunnerFactory.class).in(LazySingleton.class);

                        JacksonConfigProvider.bind(binder, WorkerBehaviorConfig.CONFIG_KEY, WorkerBehaviorConfig.class, null);
                    }
                });
    }

    /**
     */
    private static class OverlordJettyServerInitializer implements JettyServerInitializer
    {
        @Override
        public void initialize(Server server, Injector injector)
        {
            final ServletContextHandler root = new ServletContextHandler(ServletContextHandler.SESSIONS);
            root.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
            root.setInitParameter("org.eclipse.jetty.servlet.Default.redirectWelcome", "true");
            root.setWelcomeFiles(new String[]{"index.html", "console.html"});

            ServletHolder holderPwd = new ServletHolder("default", DefaultServlet.class);

            root.addServlet(holderPwd, "/");
            root.setBaseResource(
                    new ResourceCollection(
                            new String[]{
                                    TaskMaster.class.getClassLoader().getResource("static").toExternalForm(),
                            }
                    )
            );
            JettyServerInitUtils.addExtensionFilters(root, injector);
            root.addFilter(JettyServerInitUtils.defaultGzipFilterHolder(), "/*", null);

            // /status should not redirect, so add first
            root.addFilter(GuiceFilter.class, "/status/*", null);

            // redirect anything other than status to the current lead
            root.addFilter(new FilterHolder(injector.getInstance(RedirectFilter.class)), "/*", null);

            // Can't use /* here because of Guice and Jetty static content conflicts
            root.addFilter(GuiceFilter.class, "/pio/*", null);

            HandlerList handlerList = new HandlerList();
            handlerList.setHandlers(new Handler[]{JettyServerInitUtils.getJettyRequestLogHandler(), root});

            server.setHandler(handlerList);
        }
    }
}
