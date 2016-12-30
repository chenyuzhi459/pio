package io.sugo.pio.server.process;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import java.io.Serializable;

/**
 * Created by root on 16-12-27.
 */
public class Connection implements Serializable{
    private String fromOpt;
    private String fromPort;
    private String toOpt;
    private String toPort;

    public Connection(){

    }

    @JsonCreator
    public Connection(
            @JsonProperty("fromOpt") String fromOpt,
            @JsonProperty("fromPort") String fromPort,
            @JsonProperty("toOpt") String toOpt,
            @JsonProperty("toPort") String toPort
    ){
        Preconditions.checkNotNull(this.fromOpt, "Must specify a fromOpt");
        Preconditions.checkNotNull(this.fromPort, "Must specify a fromPort");
        Preconditions.checkNotNull(this.toOpt, "Must specify a toOpt");
        Preconditions.checkNotNull(this.toPort, "Must specify a toPort");
        this.fromOpt = fromOpt;
        this.fromPort = fromPort;
        this.toOpt = toOpt;
        this.toPort = toPort;
    }

    @JsonProperty("fromOpt")
    public String getFromOpt() {
        return fromOpt;
    }

    @JsonProperty("fromPort")
    public String getFromPort() {
        return fromPort;
    }

    @JsonProperty("toOpt")
    public String getToOpt() {
        return toOpt;
    }

    @JsonProperty("toPort")
    public String getToPort() {
        return toPort;
    }

    public void setFromOpt(String fromOpt) {
        this.fromOpt = fromOpt;
    }

    public void setFromPort(String fromPort) {
        this.fromPort = fromPort;
    }

    public void setToOpt(String toOpt) {
        this.toOpt = toOpt;
    }

    public void setToPort(String toPort) {
        this.toPort = toPort;
    }

    @Override
    public String toString() {
        return String.format("%s from %s:%s, to %s:%s", Connection.class.getSimpleName(),
                this.fromOpt, this.fromPort, this.toOpt, this.toOpt);
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
        if (!this.fromOpt.equals(o.fromOpt)) {
            return false;
        }
        if (!this.fromPort.equals(o.fromPort)) {
            return false;
        }
        if (!this.toOpt.equals(o.toOpt)) {
            return false;
        }
        if (!this.toPort.equals(o.toPort)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + fromOpt.hashCode();
        result = 31 * result + fromPort.hashCode();
        result = 31 * result + toOpt.hashCode();
        result = 31 * result + toPort.hashCode();
        return result;
    }
}
