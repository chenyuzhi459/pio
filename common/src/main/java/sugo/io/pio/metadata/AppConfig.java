package sugo.io.pio.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

public class AppConfig {
    @JsonProperty("env")
    private Map<String,Object> env = new HashMap();

    @JsonProperty("app")
    private Map<String,Object> app = new HashMap();

    @JsonProperty("userJar")
    private String userJar;

    @JsonProperty("mainClass")
    private String mainClass;

    public Map<String, Object> getEnv() {
        return env;
    }

    public Map<String, Object> getApp() {
        return app;
    }

    public String getUserJar() {
        return userJar;
    }

    public String getMainClass() {
        return mainClass;
    }
}
