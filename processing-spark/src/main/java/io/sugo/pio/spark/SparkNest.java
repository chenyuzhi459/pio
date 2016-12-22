package io.sugo.pio.spark;

import io.sugo.pio.spark.datahandler.HadoopContext;
import io.sugo.pio.spark.datahandler.mapreducehdfs.MapReduceHDFSHandler;
import io.sugo.pio.operator.OperatorDescription;

/**
 */
public class SparkNest extends SparkOperator {
    private volatile HadoopContext hadoopContext;

    public SparkNest(OperatorDescription description) {
        super(description);
    }

    public MapReduceHDFSHandler getMapReduceHDFSHandler() {
        return this.hadoopContext == null?null:this.hadoopContext.getMapReduceHDFSHandler();
    }
}
