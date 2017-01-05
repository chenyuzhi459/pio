package io.sugo.pio.overlord;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import io.sugo.pio.common.TaskStatus;
import io.sugo.pio.common.config.TaskConfig;
import io.sugo.pio.common.task.Task;
import io.sugo.pio.guice.Self;
import io.sugo.pio.overlord.config.ForkingTaskRunnerConfig;
import io.sugo.pio.server.PioNode;
import io.sugo.pio.worker.config.WorkerConfig;

import java.util.Properties;
import java.util.concurrent.Executor;

/**
 */
public class ForkingTaskRunner implements TaskRunner {
    @Inject
    public ForkingTaskRunner(
            ForkingTaskRunnerConfig config,
            TaskConfig taskConfig,
            WorkerConfig workerConfig,
            Properties props,
            ObjectMapper jsonMapper,
            @Self PioNode node
    ) {

    }

    @Override
    public void registerListener(TaskRunnerListener listener, Executor executor) {

    }

    @Override
    public void unregisterListener(String listenerId) {

    }

    @Override
    public ListenableFuture<TaskStatus> run(Task task) {
        return null;
    }

    @Override
    public void stop() {

    }
}
