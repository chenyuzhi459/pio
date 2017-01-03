package io.sugo.pio.spark.connections.service;

import io.sugo.pio.spark.SparkConfig;
import io.sugo.pio.spark.connections.EntryFromConfigurationBuilder;
import io.sugo.pio.spark.connections.HadoopConnectionEntry;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 */
public class HadoopConnectionService {
    public static HadoopConnectionEntry getConnectionEntry(SparkConfig sparkConfig)  {
        String path = sparkConfig.getHadoopConfigPath();
        File f = new File(path);
        try {
            return EntryFromConfigurationBuilder.createEntry(Arrays.asList(f.list()), "default");
        } catch (IOException | EntryFromConfigurationBuilder.UnsupportedValueException e) {
            throw new RuntimeException(e);
        }
    }
}
