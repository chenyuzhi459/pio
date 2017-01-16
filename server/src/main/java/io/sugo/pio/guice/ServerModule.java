package io.sugo.pio.guice;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.inject.Binder;
import com.google.inject.Provides;
import com.metamx.common.concurrent.ScheduledExecutorFactory;
import com.metamx.common.concurrent.ScheduledExecutors;
import com.metamx.common.lifecycle.Lifecycle;
import io.sugo.pio.initialization.PioModule;
import io.sugo.pio.server.PioNode;
import io.sugo.pio.server.initialization.ZkPathsConfig;

import java.util.Arrays;
import java.util.List;

/**
 */
public class ServerModule implements PioModule {
    public static final String ZK_PATHS_PROPERTY_BASE = "pio.zk.paths";

    @Override
    public void configure(Binder binder)
    {
        JsonConfigProvider.bind(binder, ZK_PATHS_PROPERTY_BASE, ZkPathsConfig.class);
        JsonConfigProvider.bind(binder, "pio", PioNode.class, Self.class);
    }

    @Provides
    @LazySingleton
    public ScheduledExecutorFactory getScheduledExecutorFactory(Lifecycle lifecycle)
    {
        return ScheduledExecutors.createFactory(lifecycle);
    }

    @Override
    public List<? extends com.fasterxml.jackson.databind.Module> getJacksonModules()
    {
        return Arrays.asList(
                new SimpleModule()
        );
    }
}
