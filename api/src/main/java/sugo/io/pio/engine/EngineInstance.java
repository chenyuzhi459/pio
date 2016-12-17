package sugo.io.pio.engine;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class EngineInstance {
    private String id;
    private String location;

    @JsonCreator
    public EngineInstance(@JsonProperty("id") String id,
                          @JsonProperty("location") String location) {
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
