package sugo.io.pio.engine;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 */
public class Engine {
    private String name;

    @JsonCreator
    public Engine(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
