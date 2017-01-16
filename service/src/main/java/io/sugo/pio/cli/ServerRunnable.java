package io.sugo.pio.cli;

import com.google.common.base.Throwables;
import com.google.inject.Injector;
import com.metamx.common.lifecycle.Lifecycle;
import com.metamx.common.logger.Logger;

/**
 */
public abstract class ServerRunnable extends GuiceRunnable {
    public ServerRunnable(Logger log)
    {
        super(log);
    }

    @Override
    public void run()
    {
        final Injector injector = makeInjector();
        final Lifecycle lifecycle = initLifecycle(injector);

        try {
            lifecycle.join();
        }
        catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
}
