package io.sugo.pio.spark;

import io.sugo.pio.spark.datahandler.mapreducehdfs.MapReduceHDFSHandler;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorDescription;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.spark.ports.SparkOutputPortImpl;

/**
 */
public class SparkOperator extends Operator implements KillableOperation {
    private SparkNest sparkNest = null;

    public SparkOperator(OperatorDescription description) {
        super(description);
    }

    public SparkNest getRadoopNest() {
        return sparkNest;
    }

    public MapReduceHDFSHandler getMapReduceHDFSHandler() {
        return getRadoopNest().getMapReduceHDFSHandler();
    }

    public OutputPort createOutputPort(String portName) {
        return createOutputPort(portName, true);
    }

    public OutputPort createOutputPort(String portName, boolean add) {
        SparkOutputPortImpl out = new SparkOutputPortImpl(getOutputPorts(), portName);
        if(add) {
            getOutputPorts().addPort(out);
        }

        return out;
    }


}
