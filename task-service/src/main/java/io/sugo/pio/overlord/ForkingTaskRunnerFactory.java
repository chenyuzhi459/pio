package io.sugo.pio.overlord;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import io.sugo.pio.common.config.TaskConfig;
import io.sugo.pio.guice.Self;
import io.sugo.pio.overlord.config.ForkingTaskRunnerConfig;
import io.sugo.pio.server.PioNode;
import io.sugo.pio.worker.config.WorkerConfig;

import java.util.Properties;

/**
 */
public class ForkingTaskRunnerFactory implements TaskRunnerFactory<ForkingTaskRunner> {
    private final ForkingTaskRunnerConfig config;
    private final TaskConfig taskConfig;
    private final WorkerConfig workerConfig;
    private final Properties props;
    private final ObjectMapper jsonMapper;
    private final PioNode node;

    @Inject
    public ForkingTaskRunnerFactory(
            final ForkingTaskRunnerConfig config,
            final TaskConfig taskConfig,
            final WorkerConfig workerConfig,
            final Properties props,
            final ObjectMapper jsonMapper,
            @Self PioNode node
    )
    {
        this.config = config;
        this.taskConfig = taskConfig;
        this.workerConfig = workerConfig;
        this.props = props;
        this.jsonMapper = jsonMapper;
        this.node = node;
    }

    @Override
    public ForkingTaskRunner build()
    {
        return new ForkingTaskRunner(config, taskConfig, workerConfig, props, jsonMapper, node);
    }
}
