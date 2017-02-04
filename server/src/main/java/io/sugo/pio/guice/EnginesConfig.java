package io.sugo.pio.guice;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 */
public class EnginesConfig {
    @JsonProperty
    @NotNull
    private boolean searchCurrentClassloader = true;

    @JsonProperty
    private String enginesDirectory = "engines";

    @JsonProperty
    private String extensionsDirectory = "engine-extensions";

    @JsonProperty
    private List<String> loadList;

    public boolean searchCurrentClassloader()
    {
        return searchCurrentClassloader;
    }

    public String getEnginesDirectory()
    {
        return enginesDirectory;
    }

    public String getExtensionsDirectory() {
        return extensionsDirectory;
    }

    public List<String> getLoadList()
    {
        return loadList;
    }

    @Override
    public String toString()
    {
        return "EnginesConfig{" +
                "searchCurrentClassloader=" + searchCurrentClassloader +
                ", enginesDirectory='" + enginesDirectory + '\'' +
                ", extensionsDirectory='" + extensionsDirectory + '\'' +
                ", loadList=" + loadList +
                '}';
    }
}
