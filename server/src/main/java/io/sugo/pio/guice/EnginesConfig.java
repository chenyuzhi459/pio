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
    private String directory = "engines";

    @JsonProperty
    private List<String> loadList;

    public boolean searchCurrentClassloader()
    {
        return searchCurrentClassloader;
    }

    public String getDirectory()
    {
        return directory;
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
                ", directory='" + directory + '\'' +
                ", loadList=" + loadList +
                '}';
    }
}
