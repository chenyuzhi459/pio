package sugo.io.pio.server;

import com.google.common.base.Optional;
import sugo.io.pio.engine.EngineInstance;

/**
 */
public interface EngineStorage {
    /**
     * Adds a engineInstance to the storage facility with a particular status.
     *
     * @param engineInstance EngineInstance
     */
    public void register(EngineInstance engineInstance);

    public Optional<EngineInstance> get(String id);
}
