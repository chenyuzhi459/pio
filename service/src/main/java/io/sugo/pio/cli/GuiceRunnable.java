package io.sugo.pio.cli;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.metamx.common.lifecycle.Lifecycle;
import com.metamx.common.logger.Logger;
import io.sugo.pio.initialization.Initialization;

import java.util.List;

/**
 */
public abstract class GuiceRunnable implements Runnable {
    private final Logger log;

    private Injector baseInjector;

    public GuiceRunnable(Logger log)
    {
        this.log = log;
    }

    @Inject
    public void configure(Injector injector)
    {
        this.baseInjector = injector;
    }

    protected abstract List<? extends Module> getModules();

    public Lifecycle initLifecycle(Injector injector)
    {
        try {
            final Lifecycle lifecycle = injector.getInstance(Lifecycle.class);

            log.info(
                    "Starting up with processors[%,d], memory[%,d].",
                    Runtime.getRuntime().availableProcessors(),
                    Runtime.getRuntime().totalMemory()
            );

            try {
                lifecycle.start();
            }
            catch (Throwable t) {
                log.error(t, "Error when starting up.  Failing.");
                log.error("Lifecycle:[%s] start error", lifecycle.getClass().getName());
                System.exit(1);
            }

            return lifecycle;
        }
        catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public Injector makeInjector()
    {
        try {
            return Initialization.makeInjectorWithModules(
                    baseInjector, getModules()
            );
        }
        catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
}
