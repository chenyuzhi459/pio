package io.sugo.pio.services;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.metamx.common.lifecycle.Lifecycle;
import io.sugo.pio.initialization.Initialization;

import java.util.List;

/**
 */
public abstract class GuiceRunnable implements Runnable {
    private Injector baseInjector;

    public GuiceRunnable()
    {
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

            try {
                lifecycle.start();
            }
            catch (Throwable t) {
                t.printStackTrace();
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
