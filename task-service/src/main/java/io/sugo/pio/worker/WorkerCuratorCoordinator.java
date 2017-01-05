package io.sugo.pio.worker;

import com.google.inject.Inject;
import io.sugo.pio.initialization.TaskZkConfig;

import java.util.Arrays;

import static org.apache.hadoop.yarn.util.StringHelper.JOINER;

/**
 */
public class WorkerCuratorCoordinator {
    private final String baseAnnouncementsPath;
    private final String baseTaskPath;
    private final String baseStatusPath;

    @Inject
    public WorkerCuratorCoordinator(
        TaskZkConfig taskZkConfig,
        Worker worker) {
        this.baseAnnouncementsPath = getPath(Arrays.asList(taskZkConfig.getAnnouncementsPath(), worker.getHost()));
        this.baseTaskPath = getPath(Arrays.asList(taskZkConfig.getTasksPath(), worker.getHost()));
        this.baseStatusPath = getPath(Arrays.asList(taskZkConfig.getStatusPath(), worker.getHost()));
    }

    public String getPath(Iterable<String> parts)
    {
        return JOINER.join(parts);
    }

    public String getAnnouncementsPathForWorker()
    {
        return baseAnnouncementsPath;
    }

    public String getTaskPathForWorker()
    {
        return baseTaskPath;
    }

    public String getTaskPathForId(String taskId)
    {
        return getPath(Arrays.asList(baseTaskPath, taskId));
    }

    public String getStatusPathForWorker()
    {
        return baseStatusPath;
    }
}
