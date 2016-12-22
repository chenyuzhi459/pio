package io.sugo.pio.spark.datahandler;

import io.sugo.pio.spark.datahandler.mapreducehdfs.MapReduceHDFSHandler;

/**
 */
public class HadoopContext {
    private final MapReduceHDFSHandler mapReduceHDFSHandler;

    public HadoopContext() {
        mapReduceHDFSHandler = new MapReduceHDFSHandler();
    }

    public MapReduceHDFSHandler getMapReduceHDFSHandler() {
        return mapReduceHDFSHandler;
    }
}
