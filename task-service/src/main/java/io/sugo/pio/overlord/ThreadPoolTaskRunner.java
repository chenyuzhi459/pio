package io.sugo.pio.overlord;

/**
 */

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.*;
import com.metamx.common.ISE;
import com.metamx.common.Pair;
import io.sugo.pio.common.TaskLocation;
import io.sugo.pio.common.TaskStatus;
import io.sugo.pio.common.config.TaskConfig;
import io.sugo.pio.common.task.Task;
import io.sugo.pio.concurrent.Execs;
import io.sugo.pio.concurrent.TaskThreadPriority;
import io.sugo.pio.guice.Self;
import io.sugo.pio.query.NoopQueryRunner;
import io.sugo.pio.query.QueryRunner;
import io.sugo.pio.query.QueryWalker;
import io.sugo.pio.server.PioNode;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 */
public class ThreadPoolTaskRunner<T> implements TaskRunner, QueryWalker {
    private final TaskConfig taskConfig;
    private final ListeningExecutorService executorService = buildExecutorService(0);
    private ThreadPoolTaskRunnerWorkItem runningItem = null;
    private final CopyOnWriteArrayList<Pair<TaskRunnerListener, Executor>> listeners = new CopyOnWriteArrayList<>();
    private final TaskLocation location;

    private volatile boolean stopping = false;

    public ThreadPoolTaskRunner(
            TaskConfig taskConfig,
            @Self PioNode node
    ) {
        this.taskConfig = taskConfig;
        this.location = TaskLocation.create(node.getHost(), node.getPort());
    }

    @Override
    public void registerListener(TaskRunnerListener listener, Executor executor) {
        for (Pair<TaskRunnerListener, Executor> pair : listeners) {
            if (pair.lhs.getListenerId().equals(listener.getListenerId())) {
                throw new ISE("Listener [%s] already registered", listener.getListenerId());
            }
        }

        final Pair<TaskRunnerListener, Executor> listenerPair = Pair.of(listener, executor);

        // Location never changes for an existing task, so it's ok to add the listener first and then issue bootstrap
        // callbacks without any special synchronization.

        listeners.add(listenerPair);
        if(null != runningItem) {
            TaskRunnerUtils.notifyLocationChanged(ImmutableList.of(listenerPair), runningItem.getTaskId(), runningItem.getLocation());
        }
    }

    @Override
    public void unregisterListener(String listenerId) {
        for (Pair<TaskRunnerListener, Executor> pair : listeners) {
            if (pair.lhs.getListenerId().equals(listenerId)) {
                listeners.remove(pair);
                return;
            }
        }
    }

    @Override
    public ListenableFuture<TaskStatus> run(Task task) {
        final ListenableFuture<TaskStatus> statusFuture = executorService
                .submit(new ThreadPoolTaskRunnerCallable(
                        task,
                        location
                ));
        final ThreadPoolTaskRunnerWorkItem taskRunnerWorkItem = new ThreadPoolTaskRunnerWorkItem(
                task,
                location,
                statusFuture
        );
        runningItem = taskRunnerWorkItem;
        Futures.addCallback(
                statusFuture, new FutureCallback<TaskStatus>()
                {
                    @Override
                    public void onSuccess(TaskStatus result)
                    {
                        runningItem = null;
                    }

                    @Override
                    public void onFailure(Throwable t)
                    {
                        runningItem = null;
                    }
                }
        );

        return statusFuture;
    }

    @Override
    public void stop() {
        stopping = true;

        try {
            executorService.shutdown();
        }
        catch (SecurityException ex) {
        }

        if(null != runningItem) {
            final Task task = runningItem.getTask();
            final long start = System.currentTimeMillis();
            if (taskConfig.isRestoreTasksOnRestart() && task.canRestore()) {
                try {
                    task.stopGracefully();
                    final TaskStatus taskStatus = runningItem.getResult().get(
                            new Interval(new DateTime(start), taskConfig.getGracefulShutdownTimeout()).toDurationMillis(),
                            TimeUnit.MILLISECONDS
                    );

                    // Ignore status, it doesn't matter for graceful shutdowns.
                    TaskRunnerUtils.notifyStatusChanged(listeners, task.getId(), taskStatus);
                }
                catch (Exception e) {
                    TaskRunnerUtils.notifyStatusChanged(listeners, task.getId(), TaskStatus.failure(task.getId()));
                }
            } else {
                TaskRunnerUtils.notifyStatusChanged(listeners, task.getId(), TaskStatus.failure(task.getId()));
            }
        }

        // Ok, now interrupt everything.
        try {
            executorService.shutdownNow();
        }
        catch (SecurityException ex) {
        }
    }

    @Override
    public Collection<TaskRunnerWorkItem> getRunningTasks()
    {
        return null == runningItem? ImmutableList.of(): ImmutableList.of(runningItem);
    }

    @Override
    public Collection<TaskRunnerWorkItem> getPendingTasks()
    {
        return ImmutableList.of();
    }

    @Override
    public Collection<TaskRunnerWorkItem> getKnownTasks()
    {
        return null == runningItem? ImmutableList.of(): ImmutableList.of(runningItem);
    }

    @Override
    public void start() {
    }

    private static ListeningExecutorService buildExecutorService(int priority)
    {
        return MoreExecutors.listeningDecorator(
                Execs.singleThreaded(
                        "task-runner-%d-priority-" + priority,
                        TaskThreadPriority.getThreadPriorityFromTaskPriority(priority)
                )
        );
    }

    @Override
    public <Q, R> QueryRunner<Q, R> getQueryRunner() {
        QueryRunner<Q, R> queryRunner = null;
        if (null != runningItem) {
            final Task task = runningItem.getTask();
            final QueryRunner<Q, R> taskQueryRunner = task.getQueryRunner();

            if (taskQueryRunner != null) {
                if (queryRunner == null) {
                    queryRunner = taskQueryRunner;
                }
            }
        }

        return queryRunner == null ? new NoopQueryRunner<Q, R>() : queryRunner;
    }

    private static class ThreadPoolTaskRunnerWorkItem extends TaskRunnerWorkItem
    {
        private static final Comparator<ThreadPoolTaskRunnerWorkItem> COMPARATOR = new Comparator<ThreadPoolTaskRunnerWorkItem>()
        {
            @Override
            public int compare(
                    ThreadPoolTaskRunnerWorkItem lhs,
                    ThreadPoolTaskRunnerWorkItem rhs
            )
            {
                return lhs.getTaskId().compareTo(rhs.getTaskId());
            }
        };

        private final Task task;
        private final TaskLocation location;

        private ThreadPoolTaskRunnerWorkItem(
                Task task,
                TaskLocation location,
                ListenableFuture<TaskStatus> result
        )
        {
            super(task.getId(), result);
            this.task = task;
            this.location = location;
        }

        public Task getTask()
        {
            return task;
        }

        @Override
        public TaskLocation getLocation()
        {
            return location;
        }
    }

    private class ThreadPoolTaskRunnerCallable implements Callable<TaskStatus>
    {
        private final Task task;
        private final TaskLocation location;

        public ThreadPoolTaskRunnerCallable(Task task, TaskLocation location)
        {
            this.task = task;
            this.location = location;
        }

        @Override
        public TaskStatus call()
        {
            final long startTime = System.currentTimeMillis();
            TaskStatus status;
            try {
                TaskRunnerUtils.notifyLocationChanged(
                        listeners,
                        task.getId(),
                        location
                );
                TaskRunnerUtils.notifyStatusChanged(listeners, task.getId(), TaskStatus.running(task.getId()));
                status = task.run();
            }
            catch (InterruptedException e) {
                status = TaskStatus.failure(task.getId());
            }
            catch (Exception e) {
                status = TaskStatus.failure(task.getId());
            }
            catch (Throwable t) {
                throw t;
            }

            status = status.withDuration(System.currentTimeMillis() - startTime);
            TaskRunnerUtils.notifyStatusChanged(listeners, task.getId(), status);
            return status;
        }
    }
}
