package io.sugo.pio.initialization;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.server.initialization.ZkPathsConfig;
import org.apache.curator.utils.ZKPaths;

/**
 */
public class TaskZkConfig {
    @JsonCreator
    public TaskZkConfig(
            @JacksonInject ZkPathsConfig zkPathsConfig,
            @JsonProperty("base") String base,
            @JsonProperty("announcementsPath") String announcementsPath,
            @JsonProperty("tasksPath") String tasksPath,
            @JsonProperty("statusPath") String statusPath,
            @JsonProperty("leaderLatchPath") String leaderLatchPath
    )
    {
        this.zkPathsConfig = zkPathsConfig;
        this.base = base;
        this.announcementsPath = announcementsPath;
        this.tasksPath = tasksPath;
        this.statusPath = statusPath;
        this.leaderLatchPath = leaderLatchPath;
    }

    @JacksonInject
    private final ZkPathsConfig zkPathsConfig;

    @JsonProperty
    private final String base;

    @JsonProperty
    private final String announcementsPath;

    @JsonProperty
    private final String tasksPath;

    @JsonProperty
    private final String statusPath;

    @JsonProperty
    private final String leaderLatchPath;

    private String defaultIndexerPath(final String subPath)
    {
        return ZKPaths.makePath(getBase(), subPath);
    }

    public String getBase()
    {
        return base == null ? getZkPathsConfig().defaultPath("indexer") : base;
    }


    public String getAnnouncementsPath()
    {
        return announcementsPath == null ? defaultIndexerPath("announcements") : announcementsPath;
    }

    public String getTasksPath()
    {
        return tasksPath == null ? defaultIndexerPath("tasks") : tasksPath;
    }

    public String getStatusPath()
    {
        return statusPath == null ? defaultIndexerPath("status") : statusPath;
    }

    public String getLeaderLatchPath()
    {
        return leaderLatchPath == null ? defaultIndexerPath("leaderLatchPath") : leaderLatchPath;
    }


    public ZkPathsConfig getZkPathsConfig()
    {
        return zkPathsConfig;
    }

}
