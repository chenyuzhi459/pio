package io.sugo.pio.engine;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 */
public class Engine {
    private String id;
    private String location;
    private String customEngineJar;
    private String customEngineClass;

    @JsonCreator
    public Engine(String id,
                  String location,
                  String userJar,
                  String userClass,
                  String customEnginJar,
                  String customEnginClass) {
        this.location = location;
        this.id = id;

        this.customEngineJar = customEnginJar;
        this.customEngineClass = customEnginClass;
    }

    public String getId() {
        return id;
    }


    public String getLocation() {
        return location;
    }

    public String getCustomEngineJar() {
        return customEngineJar;
    }

    public String getCustomEngineClass() {
        return customEngineClass;
    }
}
