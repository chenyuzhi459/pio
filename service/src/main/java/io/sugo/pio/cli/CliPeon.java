package io.sugo.pio.cli;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.metamx.common.lifecycle.Lifecycle;
import io.airlift.airline.Command;
import io.sugo.pio.guice.LifecycleModule;
import io.sugo.pio.guice.ManageLifecycle;
import io.sugo.pio.overlord.TaskRunner;
import io.sugo.pio.overlord.ThreadPoolTaskRunner;
import io.sugo.pio.server.initialization.jetty.JettyServerInitializer;
import org.eclipse.jetty.server.Server;

import java.util.List;
import java.util.Set;

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
                        binder.bind(TaskRunner.class).to(ThreadPoolTaskRunner.class);
                        binder.bind(ThreadPoolTaskRunner.class).in(ManageLifecycle.class);

                        binder.bind(JettyServerInitializer.class).to(QueryJettyServerInitializer.class);
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
//                injector.getInstance(ExecutorLifecycle.class).join();

                // Sanity check to help debug unexpected non-daemon threads
                final Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                for (Thread thread : threadSet) {
                    if (!thread.isDaemon() && thread != Thread.currentThread()) {
                    }
                }

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