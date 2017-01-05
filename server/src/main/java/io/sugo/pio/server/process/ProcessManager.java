package io.sugo.pio.server.process;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.inject.Inject;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.common.logger.Logger;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.guice.ManageLifecycle;
import io.sugo.pio.metadata.MetadataProcessManager;
import io.sugo.pio.operator.ProcessRootOperator;

import java.util.concurrent.*;

@ManageLifecycle
public class ProcessManager {

    private static final Logger log = new Logger(ProcessManager.class);

    private final BlockingQueue<OperatorProcess> queue;
    private final ProcessRunner[] runners;
    private final ProcessManagerConfig config;
    private final int executeMaxThread;
    private final Cache<String, OperatorProcess> instances;
    private final MetadataProcessManager metadataProcessManager;
    private final OperatorProcessLoader loader;

    @Inject
    public ProcessManager(ProcessManagerConfig config, MetadataProcessManager metadataProcessManager) {
        this.config = config;
        this.metadataProcessManager = metadataProcessManager;
        this.executeMaxThread = config.getExecuteMaxThread();
        queue = new ArrayBlockingQueue<>(config.getProcessQueueSize());
        this.runners = new ProcessRunner[config.getExecuteMaxThread()];
        CacheBuilder builder = CacheBuilder.newBuilder()
                .recordStats()
                .maximumSize(config.getMaxEntriesSize())
                .removalListener(new RemovalListener<String, OperatorProcess>() {
                    @Override
                    public void onRemoval(RemovalNotification<String, OperatorProcess> notification) {
                        OperatorProcess process = notification.getValue();
                        log.info("delete Process %s[%s] status:[%s] from cache", process.getName(), process.getId(), process.getStatus());
                    }
                });
        instances = builder.build();

        for (int i = 0; i < executeMaxThread; i++) {
            this.runners[i] = new ProcessRunner(queue, i, instances, metadataProcessManager);
        }
        loader = new OperatorProcessLoader(this.metadataProcessManager);
    }

    @LifecycleStart
    public void start() {
        for (int i = 0; i < config.getExecuteMaxThread(); i++) {
            this.runners[i].start();
        }
    }

    @LifecycleStop
    public void stop() {
        ExecutorService threadPool = Executors.newFixedThreadPool(executeMaxThread);
        Future<Boolean>[] futures = new Future[executeMaxThread];
        for (int i = 0; i < executeMaxThread; i++) {
            futures[i] = threadPool.submit(new ProcessRunnerShutdownHandler(runners[i]));
        }
        for (int i = 0; i < executeMaxThread; i++) {
            try {
                futures[i].get();
            } catch (InterruptedException e) {
                log.error(e, "shutdown processRunner error");
            } catch (ExecutionException e) {
                log.error(e, "shutdown processRunner error");
            }
        }
        instances.cleanUp();
    }

    class ProcessRunnerShutdownHandler implements Callable<Boolean> {
        private ProcessRunner runner;

        ProcessRunnerShutdownHandler(ProcessRunner runner) {
            this.runner = runner;
        }

        @Override
        public Boolean call() throws Exception {
            runner.shutdown();
            return Boolean.TRUE;
        }
    }

    public String register(OperatorProcess process) {
        try {
            instances.put(process.getId(), process);
            metadataProcessManager.insert(process);
            queue.offer(process, 10, TimeUnit.SECONDS);
            log.info("queue size:%d", queue.size());
            return process.getId();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public OperatorProcess get(String id) {
        loader.setProcessId(id);
        try {
            return instances.get(id, loader);
        } catch (ExecutionException e) {
            log.error(e, "get process instance %s error", id);
            throw new RuntimeException(e);
        }
    }

    class OperatorProcessLoader implements Callable<OperatorProcess> {

        private String processId;
        private final MetadataProcessManager metadataProcessManager;
        public OperatorProcessLoader(MetadataProcessManager metadataProcessManager) {
            this.metadataProcessManager = metadataProcessManager;
        }

        @Override
        public OperatorProcess call() throws Exception {
            OperatorProcess pi = metadataProcessManager.get(processId);
            return pi;
        }

        public void setProcessId(String processId) {
            this.processId = processId;
        }
    }

}
