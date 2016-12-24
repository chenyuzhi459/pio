package io.sugo.pio.spark.connections;

import io.sugo.pio.spark.HadoopTools;
import org.apache.hadoop.conf.Configuration;

/**
 */
public class HadoopConnectionEntry implements Comparable<HadoopConnectionEntry> {
    private Object configurationLock = new Object();
    private volatile Configuration configuration = null;
    private String name = "";
    private String hadoopUsername;

    public HadoopConnectionEntry() {
        hadoopUsername = HadoopTools.getNormalizedOSUsername();
    }

    @Override
    public int compareTo(HadoopConnectionEntry o) {
        return name.compareTo(o.name);
    }

    public String getHadoopUsername() {
        return hadoopUsername;
    }

    public void setHadoopUsername(String hadoopUsername) {
        this.hadoopUsername = hadoopUsername;
    }

    public Configuration getConfiguration() {
        if(configuration == null) {
            synchronized(configurationLock) {
                if(configuration == null) {
                    Configuration newConfiguration = new Configuration();
                    configuration = newConfiguration;
                }
            }
        }

        return configuration;
    }

}
