package io.sugo.pio.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import io.sugo.pio.client.task.TaskService;
import io.sugo.pio.client.task.TaskServiceSelectorConfig;
import io.sugo.pio.curator.discovery.ServerDiscoveryFactory;
import io.sugo.pio.curator.discovery.ServerDiscoverySelector;

/**
 */
public class TaskServiceDiscoveryModule implements Module
{
    @Override
    public void configure(Binder binder)
    {
        JsonConfigProvider.bind(binder, "pio.selectors.task", TaskServiceSelectorConfig.class);
    }

    @Provides
    @TaskService
    @ManageLifecycle
    public ServerDiscoverySelector getServiceProvider(
            TaskServiceSelectorConfig config,
            ServerDiscoveryFactory serverDiscoveryFactory
    )
    {
        return serverDiscoveryFactory.createSelector(config.getServiceName());
    }
}

