package io.sugo.pio.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import io.sugo.pio.concurrent.Execs;
import io.sugo.pio.overlord.TaskRunner;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 */
public class WorkerTaskMonitor {
    private static final int STOP_WARNING_SECONDS = 10;

    private final ObjectMapper jsonMapper;
    private final PathChildrenCache pathChildrenCache;
    private final CuratorFramework cf;
    private final WorkerCuratorCoordinator workerCuratorCoordinator;
    private final TaskRunner taskRunner;
    private final ExecutorService exec;

    private final CountDownLatch doneStopping = new CountDownLatch(1);
    private final Object lifecycleLock = new Object();
    private volatile boolean started = false;

    @Inject
    public WorkerTaskMonitor(
        ObjectMapper jsonMapper,
        CuratorFramework cf,
        WorkerCuratorCoordinator workerCuratorCoordinator,
        TaskRunner taskRunner
    ) {
        this.jsonMapper = jsonMapper;
        this.pathChildrenCache = new PathChildrenCache(
                cf, workerCuratorCoordinator.getTaskPathForWorker(), false, true, Execs.makeThreadFactory("TaskMonitorCache-%s")
        );
        this.cf = cf;
        this.workerCuratorCoordinator = workerCuratorCoordinator;
        this.taskRunner = taskRunner;
        this.exec = Execs.singleThreaded("WorkerTaskMonitor");
    }

    /**
     * Register a monitor for new tasks. When new tasks appear, the worker node announces a status to indicate it has
     * started the task. When the task is complete, the worker node updates the status.
     */
    @LifecycleStart
    public void start() throws Exception {
        synchronized (lifecycleLock) {
            Preconditions.checkState(!started, "already started");
            Preconditions.checkState(!exec.isShutdown(), "already stopped");
            started = true;

            try {
//                restoreRestorableTasks();
//                cleanupStaleAnnouncements();
//                registerRunListener();
//                registerLocationListener();
                pathChildrenCache.start();
                exec.submit(
                        new Runnable()
                        {
                            @Override
                            public void run()
                            {
//                                mainLoop();
                            }
                        }
                );

                started = true;
            }
            catch (InterruptedException e) {
                throw e;
            }
            catch (Exception e) {
                throw e;
            }
        }
    }

    @LifecycleStop
    public void stop() throws InterruptedException {
        synchronized (lifecycleLock) {
            Preconditions.checkState(started, "not started");

            try {
                started = false;
                taskRunner.unregisterListener("WorkerTaskMonitor");
                exec.shutdownNow();
                pathChildrenCache.close();
                taskRunner.stop();

                if (!doneStopping.await(STOP_WARNING_SECONDS, TimeUnit.SECONDS)) {
                    doneStopping.await();
                }

            } catch (InterruptedException e) {
                throw e;
            } catch (Exception e) {
            }
        }
    }
}
