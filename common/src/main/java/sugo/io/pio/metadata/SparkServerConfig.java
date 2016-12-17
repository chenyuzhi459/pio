package sugo.io.pio.metadata;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;

public class SparkServerConfig {

    @JsonProperty("historyServer")
    private String historyServer;

    @JsonProperty("eventLogDir")
    private String eventLogDir;

    @JsonProperty("historyLogDir")
    private String historyLogDir;

    public SparkServerConfig() {
        System.out.println();
    }

    public String getHistoryServer() {
        return historyServer;
    }

    public String getEventLogDir() {
        return eventLogDir;
    }

    public String getHistoryLogDir() {
        return historyLogDir;
    }

    public void setHistoryServer(String historyServer) {
        this.historyServer = historyServer;
    }

    public void setEventLogDir(String eventLogDir) {
        this.eventLogDir = eventLogDir;
    }

    public void setHistoryLogDir(String historyLogDir) {
        this.historyLogDir = historyLogDir;
    }
}
