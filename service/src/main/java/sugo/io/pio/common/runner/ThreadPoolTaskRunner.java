package sugo.io.pio.common.runner;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import sugo.io.pio.common.TaskStatus;
import sugo.io.pio.common.task.Task;
import sugo.io.pio.concurrent.Execs;
import sugo.io.pio.concurrent.TaskThreadPriority;

import java.util.concurrent.Callable;

/**
 */
public class ThreadPoolTaskRunner implements TaskRunner {
    private final ListeningExecutorService executorService;

    public ThreadPoolTaskRunner() {
        executorService = buildExecutorService(0);
    }

    @Override
    public ListenableFuture<TaskStatus> run(Task task) {
        return executorService.submit(new ThreadPoolTaskRunnerCallable(task));
    }

    private static ListeningExecutorService buildExecutorService(int priority)
    {
        return MoreExecutors.listeningDecorator(
                Execs.singleThreaded(
                        "task-runner-%d-priority-" + priority,
                        TaskThreadPriority.getThreadPriorityFromTaskPriority(priority)
                )
        );
    }

    private class ThreadPoolTaskRunnerCallable implements Callable<TaskStatus>
    {
        private final Task task;

        public ThreadPoolTaskRunnerCallable(Task task)
        {
            this.task = task;
        }

        @Override
        public TaskStatus call()
        {
            final long startTime = System.currentTimeMillis();
            TaskStatus status;
            try {
                status = task.run();
            }
            catch (InterruptedException e) {
                status = TaskStatus.failure(task.getId());
            }
            catch (Exception e) {
                status = TaskStatus.failure(task.getId());
            }
            catch (Throwable t) {
                throw t;
            }

            status = status.withDuration(System.currentTimeMillis() - startTime);
            return status;
        }
    }
}
