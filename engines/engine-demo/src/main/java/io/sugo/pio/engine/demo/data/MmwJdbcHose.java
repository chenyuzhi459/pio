package io.sugo.pio.engine.demo.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.Event;
import io.sugo.pio.jdbc.JdbcBatchEventHose;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 */
public class MmwJdbcHose extends JdbcBatchEventHose {
    private String timeColumn;
    private String url;
    private String table;
    private String username;
    private String password;
    private int count;
    private int par;
    private String[] pNames;
    public MmwJdbcHose(String timeColumn, String url, String table, String username, String password, int count, int par, String[] pNames) {
        super(timeColumn, url, table, username, password, count, par, pNames);
        this.timeColumn = timeColumn;
        this.url = url;
        this.table = table;
        this.username = username;
        this.password = password;
        this.count = count;
        this.par = par;
        this.pNames = pNames;
    }
}
