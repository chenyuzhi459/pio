package io.sugo.pio.recommend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.common.logger.Logger;
import io.sugo.pio.guice.ManageLifecycle;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.metadata.MetadataRecInstanceManager;
import io.sugo.pio.metadata.MetadataRecStrategyManager;

@ManageLifecycle
public class RecommendManager {

    private static final Logger log = new Logger(RecommendManager.class);

    private final MetadataRecInstanceManager recInstanceManager;
    private final MetadataRecStrategyManager recStrategyManager;

    private ObjectMapper jsonMapper;

    @Inject
    public RecommendManager(
            @Json ObjectMapper jsonMapper,
            MetadataRecInstanceManager recInstanceManager,
            MetadataRecStrategyManager recStrategyManager
    ) {
        this.jsonMapper = jsonMapper;
        this.recInstanceManager = recInstanceManager;
        this.recStrategyManager = recStrategyManager;
    }

    @LifecycleStart
    public void start() {
    }

    @LifecycleStop
    public void stop() {
    }
}
