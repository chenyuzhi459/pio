package io.sugo.pio.spark.datahandler;

import io.sugo.pio.spark.connections.HadoopConnectionEntry;
import io.sugo.pio.spark.datahandler.mapreducehdfs.MapReduceHDFSHandler;
import org.apache.hadoop.conf.Configuration;

/**
 */
public class HadoopContext {
    private HadoopConnectionEntry connectionEntry;
    private final Object getConfigurationLock = new Object();
    private final MapReduceHDFSHandler mapReduceHDFSHandler;
    private volatile Configuration hadoopConfiguration;

    public HadoopContext(HadoopConnectionEntry connectionEntry) {
        this.connectionEntry = connectionEntry;
        mapReduceHDFSHandler = new MapReduceHDFSHandler(this);
    }

    public Configuration getHadoopConfiguration() {
        if(hadoopConfiguration == null) {
            synchronized(getConfigurationLock) {
                if(hadoopConfiguration == null) {
                    Configuration newConfiguration = this.connectionEntry.getConfiguration();
                    hadoopConfiguration = newConfiguration;
                }
            }
        }

        return hadoopConfiguration;
    }

    public MapReduceHDFSHandler getMapReduceHDFSHandler() {
        return mapReduceHDFSHandler;
    }

    public HadoopConnectionEntry getConnectionEntry() {
        return connectionEntry;
    }
}
