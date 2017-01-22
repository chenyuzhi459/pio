package io.sugo.pio.spark;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.inject.Injector;
import io.sugo.pio.operator.OperatorChain;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.ports.metadata.SubprocessTransformRule;
import io.sugo.pio.spark.connections.HadoopConnectionEntry;
import io.sugo.pio.spark.connections.service.HadoopConnectionService;
import io.sugo.pio.spark.datahandler.mapreducehdfs.MapReduceHDFSHandler;

import java.util.List;

/**
 */
public class SparkNest extends OperatorChain {
    private final MapReduceHDFSHandler mapReduceHDFSHandler;
    private static final SparkConfig sparkConfig;

    final static Injector injector = null;

    static {
        sparkConfig = injector.getInstance(SparkConfig.class);
    }

    @JsonCreator
    public SparkNest() {
        // init the yarn connection
        HadoopConnectionEntry hadoopConnection = HadoopConnectionService.getConnectionEntry(sparkConfig);
        mapReduceHDFSHandler = new MapReduceHDFSHandler(hadoopConnection, sparkConfig);

        getTransformer().addRule(new SubprocessTransformRule(getExecutionUnit()));
    }

    @Override
    public String getFullName() {
        return "Spark Nest";
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.algorithmModel;
    }

    @Override
    public String getDescription() {
        return "Spark Nest";
    }

    public MapReduceHDFSHandler getMapReduceHDFSHandler() {
        return mapReduceHDFSHandler;
    }

    public void doWork() {
        super.doWork();
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        return types;
    }

    public boolean isCleaningEnabled() {
        return getParameterAsBoolean("cleaning");
    }
}
