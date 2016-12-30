package io.sugo.pio.server.process;

import com.google.common.cache.*;
import com.google.inject.Inject;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.common.logger.Logger;
import io.sugo.pio.Process;
import io.sugo.pio.guice.ManageLifecycle;
import io.sugo.pio.metadata.MetadataProcessInstanceManager;

import java.util.Random;
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

    @Inject
    public ProcessManager(final ProcessManagerConfig config, MetadataProcessInstanceManager metadataProcessInstanceManager) {
        this.config = config;
        this.executeMaxThread = config.getExecuteMaxThread();
        this.metadataProcessInstanceManager = metadataProcessInstanceManager;
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
            queue.offer(process, 10, TimeUnit.SECONDS);
            ProcessInstance processInstance = new ProcessInstance(process, metadataProcessInstanceManager);
            instances.put(process.getId(), processInstance);
            metadataProcessInstanceManager.insert(processInstance);
            log.info("queue size:%d", queue.size());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ProcessManagerConfig cfg = new ProcessManagerConfig();
        cfg.setExecuteMaxThread(10);
        ProcessManager manager = new ProcessManager(cfg, null);
        manager.start();
        System.out.println("stop");
        manager.stop();
        System.out.println("finish");
    }
}
