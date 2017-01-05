package io.sugo.pio.spark;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Injector;
import io.sugo.pio.guice.GuiceInjectors;
import io.sugo.pio.operator.ExecutionUnit;
import io.sugo.pio.operator.OperatorChain;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.ports.Connection;
import io.sugo.pio.ports.metadata.SubprocessTransformRule;
import io.sugo.pio.spark.connections.HadoopConnectionEntry;
import io.sugo.pio.spark.connections.service.HadoopConnectionService;
import io.sugo.pio.spark.datahandler.mapreducehdfs.MapReduceHDFSHandler;
import io.sugo.pio.operator.OperatorDescription;

import java.util.List;

/**
 */
public class SparkNest extends OperatorChain {
    private final MapReduceHDFSHandler mapReduceHDFSHandler;
    private static final SparkConfig sparkConfig;

    final static Injector injector = GuiceInjectors.makeStartupInjector();

    static {
        sparkConfig = injector.getInstance(SparkConfig.class);
    }

    @JsonCreator
    public SparkNest(@JsonProperty("connections") List<Connection> connections,
                     @JsonProperty("execUnits") List<ExecutionUnit> execUnits) {
        super(connections, execUnits, "Spark Nest", null, null);
        // init the yarn connection
        HadoopConnectionEntry hadoopConnection = HadoopConnectionService.getConnectionEntry(sparkConfig);
        mapReduceHDFSHandler = new MapReduceHDFSHandler(hadoopConnection, sparkConfig);

        getTransformer().addRule(new SubprocessTransformRule(getSubprocess(0)));
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
