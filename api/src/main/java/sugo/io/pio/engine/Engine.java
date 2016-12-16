package sugo.io.pio.engine;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 */
public class Engine {
    private String id;
    private String location;
    private String customEnginJar;
    private String customEnginClass;

    @JsonCreator
    public Engine(String id,
                  String location,
                  String userJar,
                  String userClass,
                  String customEnginJar,
                  String customEnginClass) {
        this.location = location;
        this.id = id;

        this.customEnginJar = customEnginJar;
        this.customEnginClass = customEnginClass;
    }

    public String getId() {
        return id;
    }


    public String getLocation() {
        return location;
    }

    public String getCustomEnginClass() {
        return customEnginClass;
    }

    public String getCustomEnginJar() {
        return customEnginJar;
    }
}
