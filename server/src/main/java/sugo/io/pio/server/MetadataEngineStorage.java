package sugo.io.pio.server;

import com.google.inject.Inject;
import sugo.io.pio.engine.Engine;
import sugo.io.pio.metadata.EntryExistsException;
import sugo.io.pio.metadata.SQLMetadataConnector;

/**
 */
public class MetadataEngineStorage implements EngineStorage {
    private final SQLMetadataConnector connector;

    @Inject
    public MetadataEngineStorage(SQLMetadataConnector connector) {
        this.connector = connector;
    }

    @Override
    public void insert(Engine engine) throws EntryExistsException {
    }
}
