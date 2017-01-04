package io.sugo.pio.spark.connections.service;

import io.sugo.pio.spark.SparkConfig;
import io.sugo.pio.spark.connections.EntryFromConfigurationBuilder;
import io.sugo.pio.spark.connections.HadoopConnectionEntry;

import java.io.IOException;

/**
 */
public class HadoopConnectionService {
    public static HadoopConnectionEntry getConnectionEntry(SparkConfig sparkConfig)  {
        try {
            return EntryFromConfigurationBuilder.createEntry(sparkConfig.getHadoopConfigFiles(), "default");
        } catch (IOException | EntryFromConfigurationBuilder.UnsupportedValueException e) {
            throw new RuntimeException(e);
        }
    }
}
