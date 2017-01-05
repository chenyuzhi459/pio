package io.sugo.pio.overlord;

import com.google.common.util.concurrent.ListenableFuture;
import io.sugo.pio.common.TaskStatus;
import io.sugo.pio.common.task.Task;

import java.util.concurrent.Executor;

/**
 */
public class RemoteTaskRunner implements WorkerTaskRunner {
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
