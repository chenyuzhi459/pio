package io.sugo.pio.overlord;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.metamx.common.concurrent.ScheduledExecutorFactory;
import com.metamx.http.client.HttpClient;
import io.sugo.pio.curator.cache.SimplePathChildrenCacheFactory;
import io.sugo.pio.guice.annotations.Global;
import io.sugo.pio.initialization.TaskZkConfig;
import io.sugo.pio.overlord.config.RemoteTaskRunnerConfig;
import io.sugo.pio.overlord.setup.WorkerBehaviorConfig;
import org.apache.curator.framework.CuratorFramework;

/**
 */
public class RemoteTaskRunnerFactory implements TaskRunnerFactory<RemoteTaskRunner> {
    public static final String TYPE_NAME = "remote";
    private final CuratorFramework curator;
    private final RemoteTaskRunnerConfig remoteTaskRunnerConfig;
    private final TaskZkConfig zkPaths;
    private final ObjectMapper jsonMapper;
    private final HttpClient httpClient;
    private final Supplier<WorkerBehaviorConfig> workerConfigRef;
    private final ScheduledExecutorFactory factory;

    @Inject
    public RemoteTaskRunnerFactory(
            final CuratorFramework curator,
            final RemoteTaskRunnerConfig remoteTaskRunnerConfig,
            final TaskZkConfig zkPaths,
            final ObjectMapper jsonMapper,
            @Global final HttpClient httpClient,
            final Supplier<WorkerBehaviorConfig> workerConfigRef,
            final ScheduledExecutorFactory factory) {
        this.curator = curator;
        this.remoteTaskRunnerConfig = remoteTaskRunnerConfig;
        this.zkPaths = zkPaths;
        this.jsonMapper = jsonMapper;
        this.httpClient = httpClient;
        this.workerConfigRef = workerConfigRef;
        this.factory = factory;
    }

    @Override
    public RemoteTaskRunner build() {
        return new RemoteTaskRunner(
                jsonMapper,
                remoteTaskRunnerConfig,
                zkPaths,
                curator,
                new SimplePathChildrenCacheFactory
                        .Builder()
                        .withCompressed(true)
                        .build(),
                httpClient,
                workerConfigRef,
                factory.create(1, "RemoteTaskRunner-Scheduled-Cleanup--%d")
        );
    }
}
