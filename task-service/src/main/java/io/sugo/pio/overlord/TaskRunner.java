package io.sugo.pio.overlord;

import com.google.common.util.concurrent.ListenableFuture;
import com.metamx.common.Pair;
import io.sugo.pio.common.TaskStatus;
import io.sugo.pio.common.task.Task;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;

/**
 */
public interface TaskRunner {
    /**
     * Some task runners can restart previously-running tasks after being bounced. This method does that, and returns
     * the list of tasks (and status futures).
     */
    List<Pair<Task, ListenableFuture<TaskStatus>>> restore();

    /**
     * Register a listener with this task runner. On registration, the listener will get events corresponding to the
     * current state of known tasks.
     *
     * Listener notifications are submitted to the executor in the order they occur, but it is up to the executor
     * to decide when to actually run the notifications. If your listeners will not block, feel free to use a
     * same-thread executor. Listeners that may block should use a separate executor, generally a single-threaded
     * one with a fifo queue so the order of notifications is retained.
     *
     * @param listener the listener
     * @param executor executor to run callbacks in
     */
    void registerListener(TaskRunnerListener listener, Executor executor);

    void unregisterListener(String listenerId);

    /**
     * Run a task. The returned status should be some kind of completed status.
     *
     * @param task task to run
     *
     * @return task status, eventually
     */
    ListenableFuture<TaskStatus> run(Task task);

    /**
     * Inform the task runner it can clean up any resources associated with a task. This implies shutdown of any
     * currently-running tasks.
     *
     * @param taskid task ID to clean up resources for
     */
    void shutdown(String taskid);

    /**
     * Stop this task runner. This may block until currently-running tasks can be gracefully stopped. After calling
     * stopping, "run" will not accept further tasks.
     */
    void stop();

    Collection<? extends TaskRunnerWorkItem> getRunningTasks();

    Collection<? extends TaskRunnerWorkItem> getPendingTasks();

    Collection<? extends TaskRunnerWorkItem> getKnownTasks();

    /**
     * Start the state of the runner
     */
    void start();
}
