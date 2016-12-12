package sugo.io.pio.server;

import com.google.common.base.Optional;
import sugo.io.pio.engine.Engine;

/**
 */
public interface EngineStorage {
    /**
     * Adds a engine to the storage facility with a particular status.
     *
     * @param engine Engine
     */
    public void register(Engine engine);

    public Optional<Engine> get(String id);
}
