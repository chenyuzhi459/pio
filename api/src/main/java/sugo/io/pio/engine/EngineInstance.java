package sugo.io.pio.engine;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 */
public class EngineInstance {
    private String id;
    private String location;

    @JsonCreator
    public EngineInstance(String id,
                          String location) {
        this.location = location;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }
}
