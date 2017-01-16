package io.sugo.pio.overlord;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import io.sugo.pio.common.TaskStatus;
import io.sugo.pio.common.task.Task;
import io.sugo.pio.metadata.EntryExistsException;
import io.sugo.pio.overlord.config.TaskQueueConfig;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.ReentrantLock;

/**
 */
public class TaskQueue {
    private final List<Task> tasks = Lists.newArrayList();
    private final Map<String, ListenableFuture<TaskStatus>> taskFutures = Maps.newHashMap();

    private final TaskQueueConfig config;

    private final ReentrantLock giant = new ReentrantLock(true);

    private volatile boolean active = false;

    private final ExecutorService managerExec = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder()
                    .setDaemon(false)
                    .setNameFormat("TaskQueue-Manager").build()
    );
    private final ScheduledExecutorService storageSyncExec = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder()
                    .setDaemon(false)
                    .setNameFormat("TaskQueue-StorageSync").build()
    );

    @Inject
    public TaskQueue(
        TaskQueueConfig config) {
        this.config = Preconditions.checkNotNull(config, "config");
    }

    /**
     * Adds some work to the queue and the underlying task storage facility with a generic "running" status.
     *
     * @param task task to add
     *
     * @return true
     *
     * @throws io.sugo.pio.metadata.EntryExistsException if the task already exists
     */
    public boolean add(final Task task) throws EntryExistsException {
        giant.lock();

        try {
            Preconditions.checkState(active, "Queue is not active!");
            Preconditions.checkNotNull(task, "task");
            Preconditions.checkState(tasks.size() < config.getMaxSize(), "Too many tasks (max = %,d)", config.getMaxSize());

            // If this throws with any sort of exception, including TaskExistsException, we don't want to
            // insert the task into our queue. So don't catch it.
//            taskStorage.insert(task, TaskStatus.running(task.getId()));
            addTaskInternal(task);
//            managementMayBeNecessary.signalAll();
            return true;
        } finally {
            giant.unlock();
        }
    }

    // Should always be called after taking giantLock
    private void addTaskInternal(final Task task){
        tasks.add(task);
//        taskLockbox.add(task);
    }

    // Should always be called after taking giantLock
    private void removeTaskInternal(final Task task){
//        taskLockbox.remove(task);
        tasks.remove(task);
    }
}
