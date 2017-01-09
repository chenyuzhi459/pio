package io.sugo.pio.overlord;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.metamx.common.lifecycle.Lifecycle;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import io.sugo.pio.curator.discovery.ServiceAnnouncer;
import io.sugo.pio.guice.Self;
import io.sugo.pio.initialization.TaskZkConfig;
import io.sugo.pio.overlord.config.TaskQueueConfig;
import io.sugo.pio.server.PioNode;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.recipes.leader.Participant;
import org.apache.curator.framework.state.ConnectionState;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 */
public class TaskMaster {
    private final LeaderSelector leaderSelector;
    private final ReentrantLock giant = new ReentrantLock(true);
    private final Condition mayBeStopped = giant.newCondition();

    private final AtomicReference<Lifecycle> leaderLifecycleRef = new AtomicReference<>(null);

    private volatile boolean leading = false;
    private volatile TaskRunner taskRunner;
    private volatile TaskQueue taskQueue;

    @Inject
    public TaskMaster(
        final TaskQueueConfig taskQueueConfig,
        @Self final PioNode node,
        final TaskZkConfig zkPaths,
        final TaskRunnerFactory runnerFactory,
        final CuratorFramework curator,
        final ServiceAnnouncer serviceAnnouncer) {
        this.leaderSelector = new LeaderSelector(
                curator,
                zkPaths.getLeaderLatchPath(),
                new LeaderSelectorListener()
                {
                    @Override
                    public void takeLeadership(CuratorFramework client) throws Exception
                    {
                        giant.lock();

                        try {
                            // Make sure the previous leadership cycle is really, really over.
                            stopLeading();

                            // I AM THE MASTER OF THE UNIVERSE.
//                            taskLockbox.syncFromStorage();
                            taskRunner = runnerFactory.build();
                            taskQueue = new TaskQueue(
                                    taskQueueConfig
//                                    taskStorage,
//                                    taskRunner,
//                                    taskActionClientFactory,
//                                    taskLockbox,
//                                    emitter
                            );

                            // Sensible order to start stuff:
                            final Lifecycle leaderLifecycle = new Lifecycle();

                            leaderLifecycle.addManagedInstance(taskRunner);
                            leaderLifecycle.addManagedInstance(taskQueue);
//                            leaderLifecycle.addManagedInstance(supervisorManager);

                            leaderLifecycle.addHandler(
                                    new Lifecycle.Handler()
                                    {
                                        @Override
                                        public void start() throws Exception
                                        {
                                            serviceAnnouncer.announce(node);
                                        }

                                        @Override
                                        public void stop()
                                        {
                                            serviceAnnouncer.unannounce(node);
                                        }
                                    }
                            );
                            try {
                                leaderLifecycle.start();
                                leading = true;
                                while (leading && !Thread.currentThread().isInterrupted()) {
                                    mayBeStopped.await();
                                }
                            }
                            catch (InterruptedException e) {
                                // Suppress so we can bow out gracefully
                            }
                            finally {
                                stopLeading();
                            }
                        }
                        catch (Exception e) {
                            throw Throwables.propagate(e);
                        }
                        finally {
                            giant.unlock();
                        }
                    }

                    @Override
                    public void stateChanged(CuratorFramework client, ConnectionState newState)
                    {
                        if (newState == ConnectionState.LOST || newState == ConnectionState.SUSPENDED) {
                            // disconnected from zk. assume leadership is gone
                            stopLeading();
                        }
                    }
                }
        );

        leaderSelector.setId(node.getHostAndPort());
        leaderSelector.autoRequeue();
    }

    /**
     * Starts waiting for leadership. Should only be called once throughout the life of the program.
     */
    @LifecycleStart
    public void start()
    {
        giant.lock();

        try {
            leaderSelector.start();
        }
        finally {
            giant.unlock();
        }
    }

    /**
     * Stops forever (not just this particular leadership session). Should only be called once throughout the life of
     * the program.
     */
    @LifecycleStop
    public void stop()
    {
        giant.lock();

        try {
            leaderSelector.close();
            stopLeading();
        }
        finally {
            giant.unlock();
        }
    }

    /**
     * Relinquish leadership. May be called multiple times, even when not currently the leader.
     */
    private void stopLeading()
    {
        giant.lock();

        try {
            if (leading) {
                leading = false;
                mayBeStopped.signalAll();
                final Lifecycle leaderLifecycle = leaderLifecycleRef.getAndSet(null);
                if (leaderLifecycle != null) {
                    leaderLifecycle.stop();
                }
            }
        }
        finally {
            giant.unlock();
        }
    }

    public boolean isLeading()
    {
        return leading;
    }

    public String getLeader()
    {
        try {
            final Participant leader = leaderSelector.getLeader();
            if (leader != null && leader.isLeader()) {
                return leader.getId();
            } else {
                return null;
            }
        }
        catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public Optional<TaskRunner> getTaskRunner()
    {
        if (leading) {
            return Optional.of(taskRunner);
        } else {
            return Optional.absent();
        }
    }

    public Optional<TaskQueue> getTaskQueue()
    {
        if (leading) {
            return Optional.of(taskQueue);
        } else {
            return Optional.absent();
        }
    }
}
