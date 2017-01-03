package io.sugo.pio.server.process;

import com.google.common.base.Supplier;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.inject.Inject;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.common.logger.Logger;
import io.sugo.pio.Process;
import io.sugo.pio.guice.ManageLifecycle;
import io.sugo.pio.metadata.MetadataProcessInstanceManager;

import java.util.concurrent.*;

/**
 * Created by root on 16-12-28.
 */
@ManageLifecycle
public class ProcessManager {

    private static final Logger log = new Logger(ProcessManager.class);

    private final BlockingQueue<Process> queue;
    private final ProcessRunner[] runners;
    private final ProcessManagerConfig config;
    private final int executeMaxThread;
    private final Cache<String, ProcessInstance> instances;
    private final MetadataProcessInstanceManager metadataProcessInstanceManager;
    private final ProcessInstanceLoader loader;

    @Inject
    public ProcessManager(ProcessManagerConfig config, MetadataProcessInstanceManager metadataProcessInstanceManager) {
        this.config = config;
        this.metadataProcessInstanceManager = metadataProcessInstanceManager;
        this.executeMaxThread = config.getExecuteMaxThread();
        queue = new ArrayBlockingQueue<>(config.getProcessQueueSize());
        this.runners = new ProcessRunner[config.getExecuteMaxThread()];
        CacheBuilder builder = CacheBuilder.newBuilder()
                .recordStats()
                .maximumSize(config.getMaxEntriesSize())
                .removalListener(new RemovalListener<String, ProcessInstance>() {
                    @Override
                    public void onRemoval(RemovalNotification<String, ProcessInstance> notification) {
                        ProcessInstance instance = notification.getValue();
                        log.info("delete ProcessInstance %s[%s] status:[%s] from cache", instance.getProcessName(), instance.getId(), instance.getStatus());
                    }
                });
        instances = builder.build();

        for (int i = 0; i < executeMaxThread; i++) {
            this.runners[i] = new ProcessRunner(queue, i, instances);
        }
        loader = new ProcessInstanceLoader(this.metadataProcessInstanceManager);
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

    public void register(Process process) {
        try {
            ProcessInstance processInstance = new ProcessInstance(process, metadataProcessInstanceManager);
            instances.put(process.getId(), processInstance);
            metadataProcessInstanceManager.insert(processInstance);
            queue.offer(process, 10, TimeUnit.SECONDS);
            log.info("queue size:%d", queue.size());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public ProcessInstance get(String id) {
        loader.setProcessIntanceId(id);
        ProcessInstance pi = null;
        try {
            pi = instances.get(id, loader);
        } catch (ExecutionException e) {
            log.error(e, "get process instance %s error", id);
            throw new RuntimeException(e);
        }
        return pi;
    }

    class ProcessInstanceLoader implements Callable<ProcessInstance> {

        private String processIntanceId;
        private final MetadataProcessInstanceManager metadataProcessInstanceManager;
        public ProcessInstanceLoader(MetadataProcessInstanceManager metadataProcessInstanceManager) {
            this.metadataProcessInstanceManager = metadataProcessInstanceManager;
        }

        @Override
        public ProcessInstance call() throws Exception {
            ProcessInstance pi = metadataProcessInstanceManager.get(processIntanceId);
            return pi;
        }

        public void setProcessIntanceId(String processIntanceId) {
            this.processIntanceId = processIntanceId;
        }
    }

}
