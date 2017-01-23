package io.sugo.pio.ports;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import java.io.Serializable;

public class Connection implements Serializable {
    private String fromOperator;
    private String fromPort;
    private String toOperator;
    private String toPort;

    @JsonCreator
    public Connection(
            @JsonProperty("fromOperator") String fromOperator,
            @JsonProperty("fromPort") String fromPort,
            @JsonProperty("toOperator") String toOperator,
            @JsonProperty("toPort") String toPort
    ) {
        Preconditions.checkNotNull(fromOperator, "Must specify a fromOperator");
        Preconditions.checkNotNull(fromPort, "Must specify a fromPort");
        Preconditions.checkNotNull(toOperator, "Must specify a toOperator");
        Preconditions.checkNotNull(toPort, "Must specify a toPort");
        this.fromOperator = fromOperator;
        this.fromPort = fromPort;
        this.toOperator = toOperator;
        this.toPort = toPort;
    }

    @JsonProperty("fromOperator")
    public String getFromOperator() {
        return fromOperator;
    }

    @JsonProperty("fromPort")
    public String getFromPort() {
        return fromPort;
    }

    @JsonProperty("toOperator")
    public String getToOperator() {
        return toOperator;
    }

    @JsonProperty("toPort")
    public String getToPort() {
        return toPort;
    }

    public void setFromOperator(String fromOperator) {
        this.fromOperator = fromOperator;
    }

    public void setFromPort(String fromPort) {
        this.fromPort = fromPort;
    }

    public void setToOperator(String toOperator) {
        this.toOperator = toOperator;
    }

    public void setToPort(String toPort) {
        this.toPort = toPort;
    }

    @Override
    public String toString() {
        return String.format("%s from %s:%s, to %s:%s", Connection.class.getSimpleName(),
                this.fromOperator, this.fromPort, this.toOperator, this.toPort);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Connection)) {
            return false;
        }
        Connection o = (Connection) obj;
        if (!this.fromOperator.equals(o.fromOperator)) {
            return false;
        }
        if (!this.fromPort.equals(o.fromPort)) {
            return false;
        }
        if (!this.toOperator.equals(o.toOperator)) {
            return false;
        }
        if (!this.toPort.equals(o.toPort)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = fromOperator.hashCode();
        result = 31 * result + fromPort.hashCode();
        result = 31 * result + toOperator.hashCode();
        result = 31 * result + toPort.hashCode();
        return result;
    }
}
