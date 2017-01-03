package io.sugo.pio.spark.datahandler.hdfs;

import io.sugo.pio.spark.HadoopTools;
import io.sugo.pio.spark.SparkNest;
import io.sugo.pio.spark.datahandler.mapreducehdfs.MapReduceHDFSHandler;

import java.io.Closeable;
import java.io.IOException;

/**
 */
public class TempHDFSDirectory implements Closeable {
    private final String parentDir;
    private final String subDir;
    private final boolean cleaning;

    public TempHDFSDirectory(MapReduceHDFSHandler mapReduceHDFSHandler, boolean cleaning) {
        this.subDir = "tmp_" + HadoopTools.getRandomString() + "/";
        this.parentDir = mapReduceHDFSHandler.getUserDirectory();
        this.cleaning = cleaning;
    }

    public TempHDFSDirectory(SparkNest nest) {
        this(nest.getMapReduceHDFSHandler(), nest.isCleaningEnabled());
    }

    public String getSubDir() {
        return subDir;
    }

    public String getFullPath() {
        return parentDir + subDir;
    }

    @Override
    public void close() throws IOException {
    }
}
