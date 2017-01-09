package io.sugo.pio.worker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 */
public class Worker {
    private final String host;
    private final String ip;
    private final int capacity;
    private final String version;

    @JsonCreator
    public Worker(
            @JsonProperty("host") String host,
            @JsonProperty("ip") String ip,
            @JsonProperty("capacity") int capacity,
            @JsonProperty("version") String version
    )
    {
        this.host = host;
        this.ip = ip;
        this.capacity = capacity;
        this.version = version;
    }

    @JsonProperty
    public String getHost()
    {
        return host;
    }

    @JsonProperty
    public String getIp()
    {
        return ip;
    }

    @JsonProperty
    public int getCapacity()
    {
        return capacity;
    }

    @JsonProperty
    public String getVersion()
    {
        return version;
    }

    @Override
    public String toString()
    {
        return "Worker{" +
                "host='" + host + '\'' +
                ", ip='" + ip + '\'' +
                ", capacity=" + capacity +
                ", version='" + version + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Worker worker = (Worker) o;

        if (capacity != worker.capacity) {
            return false;
        }
        if (!host.equals(worker.host)) {
            return false;
        }
        if (!ip.equals(worker.ip)) {
            return false;
        }
        return version.equals(worker.version);

    }

    @Override
    public int hashCode()
    {
        int result = host.hashCode();
        result = 31 * result + ip.hashCode();
        result = 31 * result + capacity;
        result = 31 * result + version.hashCode();
        return result;
    }
}
