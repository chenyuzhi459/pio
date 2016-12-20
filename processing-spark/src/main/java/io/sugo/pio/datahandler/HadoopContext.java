package io.sugo.pio.datahandler;

import io.sugo.pio.datahandler.mapreducehdfs.MapReduceHDFSHandler;

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
