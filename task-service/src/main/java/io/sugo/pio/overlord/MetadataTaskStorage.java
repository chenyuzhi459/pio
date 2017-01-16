package io.sugo.pio.overlord;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import io.sugo.pio.metadata.MetadataStorageTablesConfig;
import io.sugo.pio.metadata.SQLMetadataConnector;

/**
 */
public class MetadataTaskStorage implements TaskStorage {
    private final SQLMetadataConnector connector;
    private final ObjectMapper jsonMapper;
    private final MetadataStorageTablesConfig config;

    @Inject
    public MetadataTaskStorage(
            SQLMetadataConnector connector,
            ObjectMapper jsonMapper,
            MetadataStorageTablesConfig config
    ) {
        this.connector = connector;
        this.jsonMapper = jsonMapper;
        this.config = config;
    }

    @LifecycleStart
    public void start() {
//        connector.createEngineTable();
    }

    @LifecycleStop
    public void stop() {
        // do nothing
    }

}
