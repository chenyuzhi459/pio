package sugo.io.pio.metadata;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;

public class SparkServerConfig {

    @JsonProperty("yarn.historyServer.address")
    private String historyServer;

    @JsonProperty("eventLog.dir")
    private String eventLogDir;

    @JsonProperty("history.fs.logDirectory")
    private String historyLogDir = "aaaa";

    public String getHistoryServer() {
        return historyServer;
    }

    public String getEventLogDir() {
        return eventLogDir;
    }

    public String getHistoryLogDir() {
        return historyLogDir;
    }
}
