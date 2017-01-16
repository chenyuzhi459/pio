package io.sugo.pio.overlord;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.*;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Inject;
import com.metamx.common.ISE;
import com.metamx.common.Pair;
import com.metamx.common.logger.Logger;
import io.sugo.pio.common.TaskLocation;
import io.sugo.pio.common.TaskStatus;
import io.sugo.pio.common.config.TaskConfig;
import io.sugo.pio.common.task.Task;
import io.sugo.pio.concurrent.Execs;
import io.sugo.pio.guice.Self;
import io.sugo.pio.overlord.config.ForkingTaskRunnerConfig;
import io.sugo.pio.server.PioNode;
import io.sugo.pio.worker.config.WorkerConfig;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 */
public class ForkingTaskRunner implements TaskRunner {
    private static final String CHILD_PROPERTY_PREFIX = "pio.task.fork.property.";
    private static final String TASK_RESTORE_FILENAME = "restore.json";

    private final ForkingTaskRunnerConfig config;
    private final TaskConfig taskConfig;
    private final ObjectMapper jsonMapper;
    private final Properties props;
    private final PioNode node;
    private final ListeningExecutorService exec;
    private final PortFinder portFinder;
    private final CopyOnWriteArrayList<Pair<TaskRunnerListener, Executor>> listeners = new CopyOnWriteArrayList<>();

    // Writes must be synchronized. This is only a ConcurrentMap so "informational" reads can occur without waiting.
    private final Map<String, ForkingTaskRunnerWorkItem> tasks = Maps.newConcurrentMap();

    private volatile boolean stopping = false;

    @Inject
    public ForkingTaskRunner(
            ForkingTaskRunnerConfig config,
            TaskConfig taskConfig,
            WorkerConfig workerConfig,
            Properties props,
            ObjectMapper jsonMapper,
            @Self PioNode node
    ) {
        this.config = config;
        this.taskConfig = taskConfig;
        this.props = props;
        this.jsonMapper = jsonMapper;
        this.node = node;
        this.portFinder = new PortFinder(config.getStartPort());

        this.exec = MoreExecutors.listeningDecorator(
                Execs.multiThreaded(workerConfig.getCapacity(), "forking-task-runner-%d")
        );
    }

    @Override
    public void registerListener(TaskRunnerListener listener, Executor executor) {
        for (Pair<TaskRunnerListener, Executor> pair : listeners) {
            if (pair.lhs.getListenerId().equals(listener.getListenerId())) {
                throw new ISE("Listener [%s] already registered", listener.getListenerId());
            }
        }

        final Pair<TaskRunnerListener, Executor> listenerPair = Pair.of(listener, executor);

        synchronized (tasks) {
            for (ForkingTaskRunnerWorkItem item : tasks.values()) {
                TaskRunnerUtils.notifyLocationChanged(ImmutableList.of(listenerPair), item.getTaskId(), item.getLocation());
            }

            listeners.add(listenerPair);
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
        if (!tasks.containsKey(task.getId())) {
            tasks.put(
                    task.getId(),
                    new ForkingTaskRunnerWorkItem(
                            task,
                            exec.submit(
                                    new Callable<TaskStatus>() {
                                        @Override
                                        public TaskStatus call() {
                                            final String attemptUUID = UUID.randomUUID().toString();
                                            final File taskDir = taskConfig.getTaskDir(task.getId());
                                            final File attemptDir = new File(taskDir, attemptUUID);

                                            final ProcessHolder processHolder;
                                            final String childHost = node.getHost();
                                            final int childPort = portFinder.findUnusedPort();

                                            final TaskLocation taskLocation = TaskLocation.create(childHost, childPort);

                                            try {
                                                final Closer closer = Closer.create();
                                                try {
                                                    if (!attemptDir.mkdirs()) {
                                                        throw new IOException(String.format("Could not create directories: %s", attemptDir));
                                                    }

                                                    final File taskFile = new File(taskDir, "task.json");
                                                    final File statusFile = new File(attemptDir, "status.json");
                                                    final File logFile = new File(taskDir, "log");

                                                    // time to adjust process holders
                                                    synchronized (tasks) {
                                                        final ForkingTaskRunnerWorkItem taskWorkItem = tasks.get(task.getId());

                                                        if (taskWorkItem.shutdown) {
                                                            throw new IllegalStateException("Task has been shut down!");
                                                        }

                                                        if (taskWorkItem == null) {
                                                            throw new ISE("TaskInfo disappeared for task[%s]!", task.getId());
                                                        }

                                                        if (taskWorkItem.processHolder != null) {
                                                            throw new ISE("TaskInfo already has processHolder for task[%s]!", task.getId());
                                                        }

                                                        final List<String> command = Lists.newArrayList();
                                                        final String taskClasspath;
                                                        if (task.getClasspathPrefix() != null && !task.getClasspathPrefix().isEmpty()) {
                                                            taskClasspath = Joiner.on(File.pathSeparator).join(
                                                                    task.getClasspathPrefix(),
                                                                    config.getClasspath()
                                                            );
                                                        } else {
                                                            taskClasspath = config.getClasspath();
                                                        }

                                                        command.add(config.getJavaCommand());
                                                        command.add("-cp");
                                                        command.add(taskClasspath);

                                                        Iterables.addAll(command, new QuotableWhiteSpaceSplitter(config.getJavaOpts()));
                                                        Iterables.addAll(command, config.getJavaOptsArray());

                                                        // Override task specific javaOpts
                                                        Object taskJavaOpts = task.getContextValue(
                                                                ForkingTaskRunnerConfig.JAVA_OPTS_PROPERTY
                                                        );
                                                        if (taskJavaOpts != null) {
                                                            Iterables.addAll(
                                                                    command,
                                                                    new QuotableWhiteSpaceSplitter((String) taskJavaOpts)
                                                            );
                                                        }

                                                        for (String propName : props.stringPropertyNames()) {
                                                            for (String allowedPrefix : config.getAllowedPrefixes()) {
                                                                // See https://github.com/druid-io/druid/issues/1841
                                                                if (propName.startsWith(allowedPrefix)
                                                                        && !ForkingTaskRunnerConfig.JAVA_OPTS_PROPERTY.equals(propName)
                                                                        && !ForkingTaskRunnerConfig.JAVA_OPTS_ARRAY_PROPERTY.equals(propName)
                                                                        ) {
                                                                    command.add(
                                                                            String.format(
                                                                                    "-D%s=%s",
                                                                                    propName,
                                                                                    props.getProperty(propName)
                                                                            )
                                                                    );
                                                                }
                                                            }
                                                        }

                                                        // Override child JVM specific properties
                                                        for (String propName : props.stringPropertyNames()) {
                                                            if (propName.startsWith(CHILD_PROPERTY_PREFIX)) {
                                                                command.add(
                                                                        String.format(
                                                                                "-D%s=%s",
                                                                                propName.substring(CHILD_PROPERTY_PREFIX.length()),
                                                                                props.getProperty(propName)
                                                                        )
                                                                );
                                                            }
                                                        }

                                                        // Override task specific properties
                                                        final Map<String, Object> context = task.getContext();
                                                        if (context != null) {
                                                            for (String propName : context.keySet()) {
                                                                if (propName.startsWith(CHILD_PROPERTY_PREFIX)) {
                                                                    command.add(
                                                                            String.format(
                                                                                    "-D%s=%s",
                                                                                    propName.substring(CHILD_PROPERTY_PREFIX.length()),
                                                                                    task.getContextValue(propName)
                                                                            )
                                                                    );
                                                                }
                                                            }
                                                        }

                                                        command.add(String.format("-Dpio.host=%s", childHost));
                                                        command.add(String.format("-Dpio.port=%d", childPort));

                                                        //for task log4j
                                                        for (int i = 0; i < command.size(); i++) {
                                                            String arg = command.get(i);
                                                            if (arg.startsWith("-Dlog.configurationFile=")) {
                                                                command.set(i, arg.replace("-Dlog", "-Dlog4j"));
                                                            }
                                                        }
                                                        command.add("io.sugo.pio.cli.Main");
                                                        command.add("internal");
                                                        command.add("peon");
                                                        command.add(taskFile.toString());
                                                        command.add(statusFile.toString());

                                                        if (!taskFile.exists()) {
                                                            jsonMapper.writeValue(taskFile, task);
                                                        }

                                                        taskWorkItem.processHolder = new ProcessHolder(
                                                                new ProcessBuilder(ImmutableList.copyOf(command)).redirectErrorStream(true).start(),
                                                                logFile,
                                                                taskLocation.getHost(),
                                                                taskLocation.getPort()
                                                        );

                                                        processHolder = taskWorkItem.processHolder;
                                                        processHolder.registerWithCloser(closer);
                                                    }

                                                    TaskRunnerUtils.notifyLocationChanged(listeners, task.getId(), taskLocation);
                                                    TaskRunnerUtils.notifyStatusChanged(
                                                            listeners,
                                                            task.getId(),
                                                            TaskStatus.running(task.getId())
                                                    );

                                                    boolean runFailed = true;

                                                    final ByteSink logSink = Files.asByteSink(logFile, FileWriteMode.APPEND);
                                                    try (final OutputStream toLogfile = logSink.openStream()) {
                                                        ByteStreams.copy(processHolder.process.getInputStream(), toLogfile);
                                                        final int statusCode = processHolder.process.waitFor();
                                                        if (statusCode == 0) {
                                                            runFailed = false;
                                                        }
                                                    } finally {
                                                        // Upload task logs
                                                    }

                                                    TaskStatus status;
                                                    if (!runFailed) {
                                                        // Process exited successfully
                                                        status = jsonMapper.readValue(statusFile, TaskStatus.class);
                                                    } else {
                                                        // Process exited unsuccessfully
                                                        status = TaskStatus.failure(task.getId());
                                                    }

                                                    TaskRunnerUtils.notifyStatusChanged(listeners, task.getId(), status);
                                                    return status;
                                                } catch (Throwable t) {
                                                    throw closer.rethrow(t);
                                                } finally {
                                                    closer.close();
                                                }
                                            } catch (Throwable t) {
                                                throw Throwables.propagate(t);
                                            } finally {
                                                try {
                                                    synchronized (tasks) {
                                                        final ForkingTaskRunnerWorkItem taskWorkItem = tasks.remove(task.getId());
                                                        if (taskWorkItem != null && taskWorkItem.processHolder != null) {
                                                            taskWorkItem.processHolder.process.destroy();
                                                        }
                                                        if (!stopping) {
                                                            saveRunningTasks();
                                                        }
                                                    }

                                                    portFinder.markPortUnused(childPort);

                                                    try {
                                                        if (!stopping && taskDir.exists()) {
                                                            FileUtils.deleteDirectory(taskDir);
                                                        }
                                                    } catch (Exception e) {
                                                    }
                                                } catch (Exception e) {
                                                }
                                            }
                                        }
                                    }
                            )
                    )
            );
        }
        saveRunningTasks();
        return tasks.get(task.getId()).getResult();
    }

    @Override
    public void stop() {
        stopping = true;
        exec.shutdown();

        synchronized (tasks) {
            for (ForkingTaskRunnerWorkItem taskWorkItem : tasks.values()) {
                if (taskWorkItem.processHolder != null) {
                    try {
                        taskWorkItem.processHolder.process.getOutputStream().close();
                    } catch (Exception e) {
                        taskWorkItem.processHolder.process.destroy();
                    }
                }
            }
        }

        final DateTime start = new DateTime();
        final long timeout = new Interval(start, taskConfig.getGracefulShutdownTimeout()).toDurationMillis();

        // Things should be terminating now. Wait for it to happen so logs can be uploaded and all that good stuff.
        if (timeout > 0) {
            try {
                exec.awaitTermination(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public Collection<TaskRunnerWorkItem> getRunningTasks()
    {
        synchronized (tasks) {
            final List<TaskRunnerWorkItem> ret = Lists.newArrayList();
            for (final ForkingTaskRunnerWorkItem taskWorkItem : tasks.values()) {
                if (taskWorkItem.processHolder != null) {
                    ret.add(taskWorkItem);
                }
            }
            return ret;
        }
    }

    @Override
    public Collection<TaskRunnerWorkItem> getPendingTasks()
    {
        synchronized (tasks) {
            final List<TaskRunnerWorkItem> ret = Lists.newArrayList();
            for (final ForkingTaskRunnerWorkItem taskWorkItem : tasks.values()) {
                if (taskWorkItem.processHolder == null) {
                    ret.add(taskWorkItem);
                }
            }
            return ret;
        }
    }

    @Override
    public Collection<TaskRunnerWorkItem> getKnownTasks()
    {
        synchronized (tasks) {
            return Lists.<TaskRunnerWorkItem>newArrayList(tasks.values());
        }
    }

    @Override
    public void start() {

    }

    private File getRestoreFile() {
        return new File(taskConfig.getBaseTaskDir(), TASK_RESTORE_FILENAME);
    }

    // Save running tasks to a file, so they can potentially be restored on next startup. Suppresses exceptions that
    // occur while saving.
    private void saveRunningTasks() {
        final File restoreFile = getRestoreFile();
        final List<String> theTasks = Lists.newArrayList();
        for (ForkingTaskRunnerWorkItem forkingTaskRunnerWorkItem : tasks.values()) {
            theTasks.add(forkingTaskRunnerWorkItem.getTaskId());
        }

        try {
            Files.createParentDirs(restoreFile);
            jsonMapper.writeValue(restoreFile, new TaskRestoreInfo(theTasks));
        } catch (Exception e) {
        }
    }

    private static class TaskRestoreInfo {
        @JsonProperty
        private final List<String> runningTasks;

        @JsonCreator
        public TaskRestoreInfo(
                @JsonProperty("runningTasks") List<String> runningTasks
        ) {
            this.runningTasks = runningTasks;
        }

        public List<String> getRunningTasks() {
            return runningTasks;
        }
    }

    private static class ForkingTaskRunnerWorkItem extends TaskRunnerWorkItem {
        private final Task task;

        private volatile boolean shutdown = false;
        private volatile ProcessHolder processHolder = null;

        private ForkingTaskRunnerWorkItem(
                Task task,
                ListenableFuture<TaskStatus> statusFuture
        ) {
            super(task.getId(), statusFuture);
            this.task = task;
        }

        public Task getTask() {
            return task;
        }

        @Override
        public TaskLocation getLocation() {
            if (processHolder == null) {
                return TaskLocation.unknown();
            } else {
                return TaskLocation.create(processHolder.host, processHolder.port);
            }
        }
    }

    private static class ProcessHolder {
        private final Process process;
        private final File logFile;
        private final String host;
        private final int port;

        private ProcessHolder(Process process, File logFile, String host, int port) {
            this.process = process;
            this.logFile = logFile;
            this.host = host;
            this.port = port;
        }

        private void registerWithCloser(Closer closer) {
            closer.register(process.getInputStream());
            closer.register(process.getOutputStream());
        }
    }
}

/**
 * Make an iterable of space delimited strings... unless there are quotes, which it preserves
 */
class QuotableWhiteSpaceSplitter implements Iterable<String>
{
    private static final Logger LOG = new Logger(QuotableWhiteSpaceSplitter.class);
    private final String string;

    public QuotableWhiteSpaceSplitter(String string)
    {
        this.string = Preconditions.checkNotNull(string);
    }

    @Override
    public Iterator<String> iterator()
    {
        return Splitter.on(
                new CharMatcher()
                {
                    private boolean inQuotes = false;

                    @Override
                    public boolean matches(char c)
                    {
                        if ('"' == c) {
                            inQuotes = !inQuotes;
                        }
                        if (inQuotes) {
                            return false;
                        }
                        return CharMatcher.BREAKING_WHITESPACE.matches(c);
                    }
                }
        ).omitEmptyStrings().split(string).iterator();
    }
}

