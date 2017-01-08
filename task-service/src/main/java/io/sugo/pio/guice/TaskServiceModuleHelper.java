package io.sugo.pio.guice;

import com.google.inject.Binder;
import io.sugo.pio.initialization.TaskZkConfig;
import io.sugo.pio.overlord.config.ForkingTaskRunnerConfig;
import io.sugo.pio.overlord.config.RemoteTaskRunnerConfig;

/**
 */
public class TaskServiceModuleHelper {
    public static final String INDEXER_RUNNER_PROPERTY_PREFIX = "pio.task.runner";
    public static void configureTaskRunnerConfigs(Binder binder)
    {
        JsonConfigProvider.bind(binder, INDEXER_RUNNER_PROPERTY_PREFIX, ForkingTaskRunnerConfig.class);
        JsonConfigProvider.bind(binder, INDEXER_RUNNER_PROPERTY_PREFIX, RemoteTaskRunnerConfig.class);
        JsonConfigProvider.bind(binder, "pio.zk.paths.task", TaskZkConfig.class);
    }
}
