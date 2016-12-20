package sugo.io.pio.concurrent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.concurrent.*;

/**
 */
public class Execs {
    public static ExecutorService singleThreaded(@NotNull String nameFormat)
    {
        return singleThreaded(nameFormat, null);
    }

    public static ExecutorService singleThreaded(@NotNull String nameFormat, @Nullable Integer priority)
    {
        return Executors.newSingleThreadExecutor(makeThreadFactory(nameFormat, priority));
    }

    public static ExecutorService multiThreaded(int threads, @NotNull String nameFormat)
    {
        return multiThreaded(threads, nameFormat, null);
    }

    public static ExecutorService multiThreaded(int threads, @NotNull String nameFormat, @Nullable Integer priority)
    {
        return Executors.newFixedThreadPool(threads, makeThreadFactory(nameFormat, priority));
    }

    public static ScheduledExecutorService scheduledSingleThreaded(@NotNull String nameFormat)
    {
        return scheduledSingleThreaded(nameFormat, null);
    }

    public static ScheduledExecutorService scheduledSingleThreaded(@NotNull String nameFormat, @Nullable Integer priority)
    {
        return Executors.newSingleThreadScheduledExecutor(makeThreadFactory(nameFormat, priority));
    }

    public static ThreadFactory makeThreadFactory(@NotNull String nameFormat)
    {
        return makeThreadFactory(nameFormat, null);
    }

    public static ThreadFactory makeThreadFactory(@NotNull String nameFormat, @Nullable Integer priority)
    {
        final ThreadFactoryBuilder builder = new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat(nameFormat);
        if (priority != null) {
            builder.setPriority(priority);
        }
        return builder.build();
    }

    /**
     * @param nameFormat nameformat for threadFactory
     * @param capacity   maximum capacity after which the executorService will block on accepting new tasks
     *
     * @return ExecutorService which blocks accepting new tasks when the capacity reached
     */
    public static ExecutorService newBlockingSingleThreaded(final String nameFormat, final int capacity)
    {
        return newBlockingSingleThreaded(nameFormat, capacity, null);
    }

    public static ExecutorService newBlockingSingleThreaded(
            final String nameFormat,
            final int capacity,
            final Integer priority
    )
    {
        final BlockingQueue<Runnable> queue;
        if (capacity > 0) {
            queue = new ArrayBlockingQueue<>(capacity);
        } else {
            queue = new SynchronousQueue<>();
        }
        return new ThreadPoolExecutor(
                1, 1, 0L, TimeUnit.MILLISECONDS, queue, makeThreadFactory(nameFormat, priority),
                new RejectedExecutionHandler()
                {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
                    {
                        try {
                            executor.getQueue().put(r);
                        }
                        catch (InterruptedException e) {
                            throw new RejectedExecutionException("Got Interrupted while adding to the Queue");
                        }
                    }
                }
        );
    }
}
