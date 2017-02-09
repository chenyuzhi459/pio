package io.sugo.pio.jdbc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.data.input.Event;
import io.sugo.pio.engine.data.input.PropertyHose;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 */
public class JdbcPropertyHose implements PropertyHose {
    private ScalaJdbcBatchEventHose delegate;

    @JsonCreator
    public JdbcPropertyHose(  @JsonProperty("url") String url,
                              @JsonProperty("table") String table,
                              @JsonProperty("username") String username,
                              @JsonProperty("password") String password,
                              @JsonProperty("count") int count,
                              @JsonProperty("par") int par,
                              @JsonProperty("pNames") Set<String> pNames) {
        this.delegate = new ScalaJdbcBatchEventHose(null, url, table, username, password, count, par, pNames);
    }


    @Override
    public JavaRDD<Map<String, Object>> find(JavaSparkContext sc) {
        return delegate.find(sc).map(new GetEventPropertyFunction());
    }

    static class GetEventPropertyFunction implements Function<Event, Map<String, Object>>, Serializable {

        @Override
        public Map<String, Object> call(Event event) throws Exception {
            return event.getProperties();
        }
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
}
