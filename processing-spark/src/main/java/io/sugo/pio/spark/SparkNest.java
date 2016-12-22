package io.sugo.pio.spark;

import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.spark.datahandler.HadoopContext;
import io.sugo.pio.spark.datahandler.mapreducehdfs.MapReduceHDFSHandler;
import io.sugo.pio.operator.OperatorDescription;

import java.util.List;

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

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        return types;
    }
}
