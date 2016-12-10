package sugo.io.pio.services;

import com.google.common.base.Throwables;
import com.google.inject.Injector;
import com.metamx.common.lifecycle.Lifecycle;

/**
 */
public abstract class ServerRunnable extends GuiceRunnable {
    public ServerRunnable() {
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
