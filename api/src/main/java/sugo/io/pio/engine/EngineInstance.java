package sugo.io.pio.engine;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EngineInstance {
    private String id;
    private String jarPath;
    private String classPath;

    @JsonCreator
    public EngineInstance(@JsonProperty("id") String id,
                          @JsonProperty("jarPath") String jarPath,
                          @JsonProperty("classPath") String classPath) {
        this.id = id;
        this.jarPath = jarPath;
        this.classPath = classPath;
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    @JsonProperty
    public String getJarPath() {
        return jarPath;
    }

    @JsonProperty
    public String getClassPath() {
        return classPath;
    }
}
