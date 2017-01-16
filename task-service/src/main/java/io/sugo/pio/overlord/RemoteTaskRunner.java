package io.sugo.pio.overlord;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.*;
import com.google.common.collect.*;
import com.google.common.util.concurrent.*;
import com.metamx.common.ISE;
import com.metamx.common.Pair;
import com.metamx.http.client.HttpClient;
import io.sugo.pio.cache.PathChildrenCacheFactory;
import io.sugo.pio.common.TaskLocation;
import io.sugo.pio.common.TaskStatus;
import io.sugo.pio.common.task.Task;
import io.sugo.pio.concurrent.Execs;
import io.sugo.pio.curator.CuratorUtils;
import io.sugo.pio.initialization.TaskZkConfig;
import io.sugo.pio.overlord.config.RemoteTaskRunnerConfig;
import io.sugo.pio.overlord.setup.WorkerBehaviorConfig;
import io.sugo.pio.overlord.setup.WorkerSelectStrategy;
import io.sugo.pio.worker.TaskAnnouncement;
import io.sugo.pio.worker.Worker;
import org.apache.commons.lang.mutable.MutableInt;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 */
public class RemoteTaskRunner implements WorkerTaskRunner {
    private static final Joiner JOINER = Joiner.on("/");

    private final ObjectMapper jsonMapper;
    private final RemoteTaskRunnerConfig config;
    private final TaskZkConfig taskZkConfig;
    private final CuratorFramework cf;
    private final PathChildrenCacheFactory pathChildrenCacheFactory;
    private final PathChildrenCache workerPathCache;
    private final HttpClient httpClient;
    private final Supplier<WorkerBehaviorConfig> workerConfigRef;

    // all workers that exist in ZK
    private final ConcurrentMap<String, ZkWorker> zkWorkers = new ConcurrentHashMap<>();
    // payloads of pending tasks, which we remember just long enough to assign to workers
    private final ConcurrentMap<String, Task> pendingTaskPayloads = new ConcurrentHashMap<>();
    // tasks that have not yet been assigned to a worker
    private final RemoteTaskRunnerWorkQueue pendingTasks = new RemoteTaskRunnerWorkQueue();
    // all tasks that have been assigned to a worker
    private final RemoteTaskRunnerWorkQueue runningTasks = new RemoteTaskRunnerWorkQueue();
    // tasks that are complete but not cleaned up yet
    private final RemoteTaskRunnerWorkQueue completeTasks = new RemoteTaskRunnerWorkQueue();

    private final ExecutorService runPendingTasksExec;

    // Workers that have been marked as lazy. these workers are not running any tasks and can be terminated safely by the scaling policy.
    private final ConcurrentMap<String, ZkWorker> lazyWorkers = new ConcurrentHashMap<>();

    // task runner listeners
    private final CopyOnWriteArrayList<Pair<TaskRunnerListener, Executor>> listeners = new CopyOnWriteArrayList<>();
    // workers which were assigned a task and are yet to acknowledge same.
    // Map: workerId -> taskId
    private final ConcurrentMap<String, String> workersWithUnacknowledgedTask = new ConcurrentHashMap<>();
    // Map: taskId -> taskId .tasks which are being tried to be assigned to a worker
    private final ConcurrentMap<String, String> tryAssignTasks = new ConcurrentHashMap<>();

    private final Object statusLock = new Object();

    private volatile boolean started = false;

    private final ListeningScheduledExecutorService cleanupExec;

    private final ConcurrentMap<String, ScheduledFuture> removedWorkerCleanups = new ConcurrentHashMap<>();

    public RemoteTaskRunner(
        ObjectMapper jsonMapper,
        RemoteTaskRunnerConfig config,
        TaskZkConfig taskZkConfig,
        CuratorFramework cf,
        PathChildrenCacheFactory pathChildrenCacheFactory,
        HttpClient httpClient,
        Supplier<WorkerBehaviorConfig> workerConfigRef,
        ScheduledExecutorService cleanupExec
    ) {
        this.jsonMapper = jsonMapper;
        this.taskZkConfig = taskZkConfig;
        this.cf = cf;
        this.config = config;
        this.pathChildrenCacheFactory = pathChildrenCacheFactory;
        this.workerPathCache = pathChildrenCacheFactory.make(cf, taskZkConfig.getAnnouncementsPath());
        this.httpClient = httpClient;
        this.workerConfigRef = workerConfigRef;
        this.cleanupExec = MoreExecutors.listeningDecorator(cleanupExec);
        this.runPendingTasksExec = Execs.multiThreaded(
                config.getPendingTasksRunnerNumThreads(),
                "rtr-pending-tasks-runner-%d"
        );
    }

    @Override
    public void registerListener(TaskRunnerListener listener, Executor executor)
    {
        for (Pair<TaskRunnerListener, Executor> pair : listeners) {
            if (pair.lhs.getListenerId().equals(listener.getListenerId())) {
                throw new ISE("Listener [%s] already registered", listener.getListenerId());
            }
        }

        final Pair<TaskRunnerListener, Executor> listenerPair = Pair.of(listener, executor);

        synchronized (statusLock) {
            for (Map.Entry<String, RemoteTaskRunnerWorkItem> entry : runningTasks.entrySet()) {
                TaskRunnerUtils.notifyLocationChanged(
                        ImmutableList.of(listenerPair),
                        entry.getKey(),
                        entry.getValue().getLocation()
                );
            }

            listeners.add(listenerPair);
        }
    }

    @Override
    public void unregisterListener(String listenerId)
    {
        for (Pair<TaskRunnerListener, Executor> pair : listeners) {
            if (pair.lhs.getListenerId().equals(listenerId)) {
                listeners.remove(pair);
                return;
            }
        }
    }


    public ZkWorker findWorkerRunningTask(String taskId)
    {
        for (ZkWorker zkWorker : zkWorkers.values()) {
            if (zkWorker.isRunningTask(taskId)) {
                return zkWorker;
            }
        }
        return null;
    }

    public boolean isWorkerRunningTask(Worker worker, String taskId)
    {
        ZkWorker zkWorker = zkWorkers.get(worker.getHost());
        return (zkWorker != null && zkWorker.isRunningTask(taskId));
    }

    @Override
    public ListenableFuture<TaskStatus> run(Task task) {
        final RemoteTaskRunnerWorkItem completeTask, runningTask, pendingTask;
        if ((pendingTask = pendingTasks.get(task.getId())) != null) {
            return pendingTask.getResult();
        } else if ((runningTask = runningTasks.get(task.getId())) != null) {
            ZkWorker zkWorker = findWorkerRunningTask(task.getId());
            if (zkWorker != null) {
                TaskAnnouncement announcement = zkWorker.getRunningTasks().get(task.getId());
                if (announcement.getTaskStatus().isComplete()) {
                    taskComplete(runningTask, zkWorker, announcement.getTaskStatus());
                }
            }
            return runningTask.getResult();
        } else if ((completeTask = completeTasks.get(task.getId())) != null) {
            return completeTask.getResult();
        } else {
            return addPendingTask(task).getResult();
        }
    }

    /**
     * Adds a task to the pending queue
     */
    private RemoteTaskRunnerWorkItem addPendingTask(final Task task)
    {
        final RemoteTaskRunnerWorkItem taskRunnerWorkItem = new RemoteTaskRunnerWorkItem(task.getId(), null, null);
        pendingTaskPayloads.put(task.getId(), task);
        pendingTasks.put(task.getId(), taskRunnerWorkItem);
        runPendingTasks();
        return taskRunnerWorkItem;
    }

    /**
     * This method uses a multi-threaded executor to extract all pending tasks and attempt to run them. Any tasks that
     * are successfully assigned to a worker will be moved from pendingTasks to runningTasks. This method is thread-safe.
     * This method should be run each time there is new worker capacity or if new tasks are assigned.
     */
    private void runPendingTasks()
    {
        runPendingTasksExec.submit(
                new Callable<Void>()
                {
                    @Override
                    public Void call() throws Exception
                    {
                        try {
                            // make a copy of the pending tasks because tryAssignTask may delete tasks from pending and move them
                            // into running status
                            List<RemoteTaskRunnerWorkItem> copy = Lists.newArrayList(pendingTasks.values());
                            for (RemoteTaskRunnerWorkItem taskRunnerWorkItem : copy) {
                                String taskId = taskRunnerWorkItem.getTaskId();
                                if (tryAssignTasks.putIfAbsent(taskId, taskId) == null) {
                                    try {
                                        //this can still be null due to race from explicit task shutdown request
                                        //or if another thread steals and completes this task right after this thread makes copy
                                        //of pending tasks. See https://github.com/druid-io/druid/issues/2842 .
                                        Task task = pendingTaskPayloads.get(taskId);
                                        if (task != null && tryAssignTask(task, taskRunnerWorkItem)) {
                                            pendingTaskPayloads.remove(taskId);
                                        }
                                    }
                                    catch (Exception e) {
                                        RemoteTaskRunnerWorkItem workItem = pendingTasks.remove(taskId);
                                        if (workItem != null) {
                                            taskComplete(workItem, null, TaskStatus.failure(taskId));
                                        }
                                    }
                                    finally {
                                        tryAssignTasks.remove(taskId);
                                    }
                                }
                            }
                        }
                        catch (Exception e) {
                        }

                        return null;
                    }
                }
        );
    }

    @Override
    public void stop() {
        try {
            if (!started) {
                return;
            }
            started = false;

            for (ZkWorker zkWorker : zkWorkers.values()) {
                zkWorker.close();
            }
            workerPathCache.close();

            if (runPendingTasksExec != null) {
                runPendingTasksExec.shutdown();
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public Collection<? extends TaskRunnerWorkItem> getRunningTasks() {
        return ImmutableList.copyOf(runningTasks.values());
    }

    @Override
    public Collection<? extends TaskRunnerWorkItem> getPendingTasks() {
        return ImmutableList.copyOf(pendingTasks.values());
    }

    @Override
    public Collection<? extends TaskRunnerWorkItem> getKnownTasks() {
        // Racey, since there is a period of time during assignment when a task is neither pending nor running
        return ImmutableList.copyOf(Iterables.concat(pendingTasks.values(), runningTasks.values(), completeTasks.values()));
    }

    @Override
    public void start() {
        try {
            if (started) {
                return;
            }

            final MutableInt waitingFor = new MutableInt(1);
            final Object waitingForMonitor = new Object();

            // Add listener for creation/deletion of workers
            workerPathCache.getListenable().addListener(
                    new PathChildrenCacheListener()
                    {
                        @Override
                        public void childEvent(CuratorFramework client, final PathChildrenCacheEvent event) throws Exception
                        {
                            final Worker worker;
                            switch (event.getType()) {
                                case CHILD_ADDED:
                                    worker = jsonMapper.readValue(
                                            event.getData().getData(),
                                            Worker.class
                                    );
                                    synchronized (waitingForMonitor) {
                                        waitingFor.increment();
                                    }
                                    Futures.addCallback(
                                            addWorker(worker),
                                            new FutureCallback<ZkWorker>()
                                            {
                                                @Override
                                                public void onSuccess(ZkWorker zkWorker)
                                                {
                                                    synchronized (waitingForMonitor) {
                                                        waitingFor.decrement();
                                                        waitingForMonitor.notifyAll();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Throwable throwable)
                                                {
                                                    synchronized (waitingForMonitor) {
                                                        waitingFor.decrement();
                                                        waitingForMonitor.notifyAll();
                                                    }
                                                }
                                            }
                                    );
                                    break;
                                case CHILD_UPDATED:
                                    worker = jsonMapper.readValue(
                                            event.getData().getData(),
                                            Worker.class
                                    );
                                    updateWorker(worker);
                                    break;

                                case CHILD_REMOVED:
                                    worker = jsonMapper.readValue(
                                            event.getData().getData(),
                                            Worker.class
                                    );
                                    removeWorker(worker);
                                    break;
                                case INITIALIZED:
                                    // Schedule cleanup for task status of the workers that might have disconnected while overlord was not running
                                    List<String> workers;
                                    try {
                                        workers = cf.getChildren().forPath(taskZkConfig.getStatusPath());
                                    }
                                    catch (KeeperException.NoNodeException e) {
                                        // statusPath doesn't exist yet; can occur if no middleManagers have started.
                                        workers = ImmutableList.of();
                                    }
                                    for (String workerId : workers) {
                                        final String workerAnnouncePath = JOINER.join(taskZkConfig.getAnnouncementsPath(), workerId);
                                        final String workerStatusPath = JOINER.join(taskZkConfig.getStatusPath(), workerId);
                                        if (!zkWorkers.containsKey(workerId) && cf.checkExists().forPath(workerAnnouncePath) == null) {
                                            try {
                                                scheduleTasksCleanupForWorker(workerId, cf.getChildren().forPath(workerStatusPath));
                                            }
                                            catch (Exception e) {

                                            }
                                        }
                                    }
                                    synchronized (waitingForMonitor) {
                                        waitingFor.decrement();
                                        waitingForMonitor.notifyAll();
                                    }
                                default:
                                    break;
                            }
                        }
                    }
            );
            workerPathCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            synchronized (waitingForMonitor) {
                while (waitingFor.intValue() > 0) {
                    waitingForMonitor.wait();
                }
            }
            started = true;
        }
        catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * Ensures no workers are already running a task before assigning the task to a worker.
     * It is possible that a worker is running a task that the RTR has no knowledge of. This occurs when the RTR
     * needs to bootstrap after a restart.
     *
     * @param taskRunnerWorkItem - the task to assign
     *
     * @return true iff the task is now assigned
     */
    private boolean tryAssignTask(final Task task, final RemoteTaskRunnerWorkItem taskRunnerWorkItem) throws Exception
    {
        Preconditions.checkNotNull(task, "task");
        Preconditions.checkNotNull(taskRunnerWorkItem, "taskRunnerWorkItem");
        Preconditions.checkArgument(task.getId().equals(taskRunnerWorkItem.getTaskId()), "task id != workItem id");

        if (runningTasks.containsKey(task.getId()) || findWorkerRunningTask(task.getId()) != null) {
            return true;
        } else {
            // Nothing running this task, announce it in ZK for a worker to run it
            WorkerBehaviorConfig workerConfig = workerConfigRef.get();
            WorkerSelectStrategy strategy;
            if (workerConfig == null || workerConfig.getSelectStrategy() == null) {
                strategy = WorkerBehaviorConfig.DEFAULT_STRATEGY;
            } else {
                strategy = workerConfig.getSelectStrategy();
            }

            ZkWorker assignedWorker = null;
            Optional<ImmutableWorkerInfo> immutableZkWorker = null;
            try {
                synchronized (workersWithUnacknowledgedTask) {
                    immutableZkWorker = strategy.findWorkerForTask(
                            config,
                            ImmutableMap.copyOf(
                                    Maps.transformEntries(
                                            Maps.filterEntries(
                                                    zkWorkers, new Predicate<Map.Entry<String, ZkWorker>>()
                                                    {
                                                        @Override
                                                        public boolean apply(Map.Entry<String, ZkWorker> input)
                                                        {
                                                            return !lazyWorkers.containsKey(input.getKey()) &&
                                                                    !workersWithUnacknowledgedTask.containsKey(input.getKey());
                                                        }
                                                    }
                                            ),
                                            new Maps.EntryTransformer<String, ZkWorker, ImmutableWorkerInfo>()
                                            {
                                                @Override
                                                public ImmutableWorkerInfo transformEntry(
                                                        String key, ZkWorker value
                                                )
                                                {
                                                    return value.toImmutable();
                                                }
                                            }
                                    )
                            ),
                            task
                    );

                    if (immutableZkWorker.isPresent() &&
                            workersWithUnacknowledgedTask.putIfAbsent(immutableZkWorker.get().getWorker().getHost(), task.getId())
                                    == null) {
                        assignedWorker = zkWorkers.get(immutableZkWorker.get().getWorker().getHost());
                    }
                }

                if (assignedWorker != null) {
                    return announceTask(task, assignedWorker, taskRunnerWorkItem);
                } else {
                }

                return false;
            }
            finally {
                if (assignedWorker != null) {
                    workersWithUnacknowledgedTask.remove(assignedWorker.getWorker().getHost());
                    //if this attempt won the race to run the task then other task might be able to use this worker now after task ack.
                    runPendingTasks();
                }
            }
        }
    }

    /**
     * Creates a ZK entry under a specific path associated with a worker. The worker is responsible for
     * removing the task ZK entry and creating a task status ZK entry.
     *
     * @param theZkWorker        The worker the task is assigned to
     * @param taskRunnerWorkItem The task to be assigned
     *
     * @return boolean indicating whether the task was successfully assigned or not
     */
    private boolean announceTask(
            final Task task,
            final ZkWorker theZkWorker,
            final RemoteTaskRunnerWorkItem taskRunnerWorkItem
    ) throws Exception
    {
        Preconditions.checkArgument(task.getId().equals(taskRunnerWorkItem.getTaskId()), "task id != workItem id");
        final String worker = theZkWorker.getWorker().getHost();
        synchronized (statusLock) {
            if (!zkWorkers.containsKey(worker) || lazyWorkers.containsKey(worker)) {
                // the worker might got killed or has been marked as lazy.
                return false;
            }

            CuratorUtils.createIfNotExists(
                    cf,
                    JOINER.join(taskZkConfig.getTasksPath(), worker, task.getId()),
                    CreateMode.EPHEMERAL,
                    jsonMapper.writeValueAsBytes(task),
                    config.getMaxZnodeBytes()
            );

            RemoteTaskRunnerWorkItem workItem = pendingTasks.remove(task.getId());
            if (workItem == null) {
                return false;
            }

            RemoteTaskRunnerWorkItem newWorkItem = workItem.withWorker(theZkWorker.getWorker(), null);
            runningTasks.put(task.getId(), newWorkItem);
            TaskRunnerUtils.notifyStatusChanged(listeners, task.getId(), TaskStatus.running(task.getId()));

            // Syncing state with Zookeeper - don't assign new tasks until the task we just assigned is actually running
            // on a worker - this avoids overflowing a worker with tasks
            Stopwatch timeoutStopwatch = Stopwatch.createStarted();
            while (!isWorkerRunningTask(theZkWorker.getWorker(), task.getId())) {
                final long waitMs = config.getTaskAssignmentTimeout().toStandardDuration().getMillis();
                statusLock.wait(waitMs);
                long elapsed = timeoutStopwatch.elapsed(TimeUnit.MILLISECONDS);
                if (elapsed >= waitMs) {
                    taskComplete(taskRunnerWorkItem, theZkWorker, TaskStatus.failure(task.getId()));
                    break;
                }
            }
            return true;
        }
    }

    private boolean cancelWorkerCleanup(String workerHost)
    {
        ScheduledFuture previousCleanup = removedWorkerCleanups.remove(workerHost);
        if (previousCleanup != null) {
            previousCleanup.cancel(false);
        }
        return previousCleanup != null;
    }

    /**
     * When a new worker appears, listeners are registered for status changes associated with tasks assigned to
     * the worker. Status changes indicate the creation or completion of a task.
     * The RemoteTaskRunner updates state according to these changes.
     *
     * @param worker contains metadata for a worker that has appeared in ZK
     *
     * @return future that will contain a fully initialized worker
     */
    private ListenableFuture<ZkWorker> addWorker(final Worker worker)
    {
        try {
            cancelWorkerCleanup(worker.getHost());

            final String workerStatusPath = JOINER.join(taskZkConfig.getStatusPath(), worker.getHost());
            final PathChildrenCache statusCache = pathChildrenCacheFactory.make(cf, workerStatusPath);
            final SettableFuture<ZkWorker> retVal = SettableFuture.create();
            final ZkWorker zkWorker = new ZkWorker(
                    worker,
                    statusCache,
                    jsonMapper
            );

            // Add status listener to the watcher for status changes
            zkWorker.addListener(
                    new PathChildrenCacheListener()
                    {
                        @Override
                        public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception
                        {
                            final String taskId;
                            final RemoteTaskRunnerWorkItem taskRunnerWorkItem;
                            synchronized (statusLock) {
                                try {
                                    switch (event.getType()) {
                                        case CHILD_ADDED:
                                        case CHILD_UPDATED:
                                            taskId = ZKPaths.getNodeFromPath(event.getData().getPath());
                                            final TaskAnnouncement announcement = jsonMapper.readValue(
                                                    event.getData().getData(), TaskAnnouncement.class
                                            );

                                            // Synchronizing state with ZK
                                            statusLock.notifyAll();

                                            final RemoteTaskRunnerWorkItem tmp;
                                            if ((tmp = runningTasks.get(taskId)) != null) {
                                                taskRunnerWorkItem = tmp;
                                            } else {
                                                final RemoteTaskRunnerWorkItem newTaskRunnerWorkItem = new RemoteTaskRunnerWorkItem(
                                                        taskId,
                                                        zkWorker.getWorker(),
                                                        TaskLocation.unknown()
                                                );
                                                final RemoteTaskRunnerWorkItem existingItem = runningTasks.putIfAbsent(
                                                        taskId,
                                                        newTaskRunnerWorkItem
                                                );
                                                if (existingItem == null) {
                                                    taskRunnerWorkItem = newTaskRunnerWorkItem;
                                                } else {
                                                    taskRunnerWorkItem = existingItem;
                                                }
                                            }

                                            if (!announcement.getTaskLocation().equals(taskRunnerWorkItem.getLocation())) {
                                                taskRunnerWorkItem.setLocation(announcement.getTaskLocation());
                                                TaskRunnerUtils.notifyLocationChanged(listeners, taskId, announcement.getTaskLocation());
                                            }

                                            if (announcement.getTaskStatus().isComplete()) {
                                                taskComplete(taskRunnerWorkItem, zkWorker, announcement.getTaskStatus());
                                                runPendingTasks();
                                            }
                                            break;
                                        case CHILD_REMOVED:
                                            taskId = ZKPaths.getNodeFromPath(event.getData().getPath());
                                            taskRunnerWorkItem = runningTasks.remove(taskId);
                                            if (taskRunnerWorkItem != null) {
                                                taskRunnerWorkItem.setResult(TaskStatus.failure(taskId));
                                                TaskRunnerUtils.notifyStatusChanged(listeners, taskId, TaskStatus.failure(taskId));
                                            }
                                            break;
                                        case INITIALIZED:
                                            if (zkWorkers.putIfAbsent(worker.getHost(), zkWorker) == null) {
                                                retVal.set(zkWorker);
                                            } else {
                                                final String message = String.format(
                                                        "WTF?! Tried to add already-existing worker[%s]",
                                                        worker.getHost()
                                                );
                                                retVal.setException(new IllegalStateException(message));
                                            }
                                            runPendingTasks();
                                    }
                                }
                                catch (Exception e) {

                                }
                            }
                        }
                    }
            );
            zkWorker.start();
            return retVal;
        }
        catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * We allow workers to change their own capacities and versions. They cannot change their own hosts or ips without
     * dropping themselves and re-announcing.
     */
    private void updateWorker(final Worker worker)
    {
        final ZkWorker zkWorker = zkWorkers.get(worker.getHost());
        if (zkWorker != null) {
            zkWorker.setWorker(worker);
        }
    }

    /**
     * When a ephemeral worker node disappears from ZK, incomplete running tasks will be retried by
     * the logic in the status listener. We still have to make sure there are no tasks assigned
     * to the worker but not yet running.
     *
     * @param worker - the removed worker
     */
    private void removeWorker(final Worker worker)
    {
        final ZkWorker zkWorker = zkWorkers.get(worker.getHost());
        if (zkWorker != null) {
            try {
                scheduleTasksCleanupForWorker(worker.getHost(), getAssignedTasks(worker));
            }
            catch (Exception e) {
                throw Throwables.propagate(e);
            }
            finally {
                try {
                    zkWorker.close();
                }
                catch (Exception e) {
                }
                zkWorkers.remove(worker.getHost());
            }
        }
        lazyWorkers.remove(worker.getHost());
    }

    /**
     * Schedule a task that will, at some point in the future, clean up znodes and issue failures for "tasksToFail"
     * if they are being run by "worker".
     */
    private void scheduleTasksCleanupForWorker(final String worker, final List<String> tasksToFail)
    {
        // This method is only called from the PathChildrenCache event handler, so this may look like a race,
        // but is actually not.
        cancelWorkerCleanup(worker);

        final ListenableScheduledFuture<?> cleanupTask = cleanupExec.schedule(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try {
                            for (String assignedTask : tasksToFail) {
                                String taskPath = JOINER.join(taskZkConfig.getTasksPath(), worker, assignedTask);
                                String statusPath = JOINER.join(taskZkConfig.getStatusPath(), worker, assignedTask);
                                if (cf.checkExists().forPath(taskPath) != null) {
                                    cf.delete().guaranteed().forPath(taskPath);
                                }

                                if (cf.checkExists().forPath(statusPath) != null) {
                                    cf.delete().guaranteed().forPath(statusPath);
                                }

                                RemoteTaskRunnerWorkItem taskRunnerWorkItem = runningTasks.remove(assignedTask);
                                if (taskRunnerWorkItem != null) {
                                    taskRunnerWorkItem.setResult(TaskStatus.failure(assignedTask));
                                    TaskRunnerUtils.notifyStatusChanged(listeners, assignedTask, TaskStatus.failure(assignedTask));
                                }
                            }

                            // worker is gone, remove worker task status announcements path.
                            String workerStatusPath = JOINER.join(taskZkConfig.getStatusPath(), worker);
                            if (cf.checkExists().forPath(workerStatusPath) != null) {
                                cf.delete().guaranteed().forPath(JOINER.join(taskZkConfig.getStatusPath(), worker));
                            }
                        }
                        catch (Exception e) {
                            throw Throwables.propagate(e);
                        }
                    }
                },
                config.getTaskCleanupTimeout().toStandardDuration().getMillis(),
                TimeUnit.MILLISECONDS
        );

        removedWorkerCleanups.put(worker, cleanupTask);

        // Remove this entry from removedWorkerCleanups when done, if it's actually the one in there.
        Futures.addCallback(
                cleanupTask,
                new FutureCallback<Object>()
                {
                    @Override
                    public void onSuccess(Object result)
                    {
                        removedWorkerCleanups.remove(worker, cleanupTask);
                    }

                    @Override
                    public void onFailure(Throwable t)
                    {
                        removedWorkerCleanups.remove(worker, cleanupTask);
                    }
                }
        );
    }

    private void taskComplete(
            RemoteTaskRunnerWorkItem taskRunnerWorkItem,
            ZkWorker zkWorker,
            TaskStatus taskStatus
    )
    {
        Preconditions.checkNotNull(taskRunnerWorkItem, "taskRunnerWorkItem");
        Preconditions.checkNotNull(taskStatus, "taskStatus");
        if (zkWorker != null) {
            // Worker is done with this task
            zkWorker.setLastCompletedTaskTime(new DateTime());
        }

        // Move from running -> complete
        completeTasks.put(taskStatus.getId(), taskRunnerWorkItem);
        runningTasks.remove(taskStatus.getId());

        // Notify interested parties
        taskRunnerWorkItem.setResult(taskStatus);
        TaskRunnerUtils.notifyStatusChanged(listeners, taskStatus.getId(), taskStatus);
    }

    protected List<String> getAssignedTasks(Worker worker) throws Exception
    {
        final List<String> assignedTasks = Lists.newArrayList(
                cf.getChildren().forPath(JOINER.join(taskZkConfig.getTasksPath(), worker.getHost()))
        );

        for (Map.Entry<String, RemoteTaskRunnerWorkItem> entry : runningTasks.entrySet()) {
            if (entry.getValue() == null) {
            } else if (entry.getValue().getWorker().getHost().equalsIgnoreCase(worker.getHost())) {
                assignedTasks.add(entry.getKey());
            }
        }
        return assignedTasks;
    }
}
