package sugo.io.pio.server;

import sugo.io.pio.engine.Engine;
import sugo.io.pio.metadata.EntryExistsException;

/**
 */
public interface EngineStorage {
    /**
     * Adds a task to the storage facility with a particular status.
     *
     * @param engine task status
     * @throws sugo.io.pio.metadata.EntryExistsException if the task ID already exists
     */
    public void insert(Engine engine) throws EntryExistsException;
}
