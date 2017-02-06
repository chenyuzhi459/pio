package io.sugo.pio.jdbc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.Event;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Set;

/**
 */
public class JdbcBatchEventHose implements BatchEventHose {
    private ScalaJdbcBatchEventHose delegate;

    @JsonCreator
    public JdbcBatchEventHose(@JsonProperty("timeColumn") String timeColumn,
                               @JsonProperty("url") String url,
                               @JsonProperty("table") String table,
                               @JsonProperty("username") String username,
                               @JsonProperty("password") String password,
                               @JsonProperty("count") int count,
                               @JsonProperty("par") int par,
                               @JsonProperty("pNames") Set<String> pNames) {
        this.delegate = new ScalaJdbcBatchEventHose(timeColumn, url, table, username, password, count, par, pNames);
    }

    @Override
    public JavaRDD<Event> find(JavaSparkContext sc) {
        return delegate.find(sc);
    }

    @JsonProperty
    public String getTimeColumn() {
        return delegate.getTimeColumn();
    }

    @JsonProperty
    public String getUrl() {
        return delegate.getUrl();
    }

    @JsonProperty
    public String getTable() {
        return delegate.getTable();
    }

    @JsonProperty
    public String getUsername() {
        return delegate.getUsername();
    }

    @JsonProperty
    public String getPassword() {
        return delegate.getPassword();
    }

    @JsonProperty
    public int getCount() {
        return delegate.getCount();
    }

    @JsonProperty
    public int getPar() {
        return delegate.getPar();
    }

    @JsonProperty
    public Set<String> getPNames() {
        return delegate.getPNames();
    }

    @Override
    public JavaRDD<Event> find(JavaSparkContext sc, long starttime, long endTime) {
        return delegate.find(sc, starttime, endTime);
    }
}
