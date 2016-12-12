package sugo.io.pio.engine;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 */
public class Engine {
    private String id;
    private String location;

    @JsonCreator
    public Engine(String id,
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
