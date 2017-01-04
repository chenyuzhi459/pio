package io.sugo.pio.server.process;

import com.google.common.cache.Cache;
import com.metamx.common.logger.Logger;
import io.sugo.pio.Process;
import io.sugo.pio.metadata.MetadataProcessInstanceManager;
import io.sugo.pio.operator.IOContainer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ProcessRunner implements Runnable {
    private static final Logger log = new Logger(ProcessRunner.class);
    private final BlockingQueue<Process> queue;
    private volatile boolean running = false;
    private Thread t;
    private final CountDownLatch latch;
    public static final int WAIT_TIME = 2;
    private final String name;
    private final Cache<String, ProcessInstance> instances;

    public ProcessRunner(final BlockingQueue<Process> queue, int index,
                         Cache<String, ProcessInstance> instances
    ) {
        this.queue = queue;
        this.instances = instances;
        name = "PIO-Process-Runner-" + index;
        log.info("start Thread:%s", name);
        t = new Thread(this, name);
        this.latch = new CountDownLatch(1);
    }

    public void start() {
        this.running = true;
        this.t.start();
    }

    public void shutdown() {
        this.running = false;
        try {
            this.latch.await(WAIT_TIME + 1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("stop thread:%s", name);
    }

    @Override
    public void run() {
        while (running) {
            try {
                Process process = queue.poll(WAIT_TIME, TimeUnit.SECONDS);
                if (process != null) {
                    log.info("queue size:%d", queue.size());
                    ProcessInstance processInstance = instances.getIfPresent(process.getId());
                    try {
                        processInstance.run();
                    } catch (RuntimeException re) {
                        log.error(re, "Process %s run failed", process.getId());
                        processInstance.failed();
                    }
                    IOContainer ret = process.getRootOperator().getResults(true);
                    processInstance.success();
                    log.info("IOContainer:%s", ret.toString());

                    log.info("Process:%s finished", process.getId());
                }
            } catch (InterruptedException e) {
                log.error(e, "Thread %s Interrupted", Thread.currentThread().getName());
            } catch (Exception e) {
                log.error(e, "Thread %s unknown exception", Thread.currentThread().getName());
            }
        }
        this.latch.countDown();
    }
}