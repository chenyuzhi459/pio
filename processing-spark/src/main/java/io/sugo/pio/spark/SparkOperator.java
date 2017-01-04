package io.sugo.pio.spark;

import io.sugo.pio.operator.*;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.spark.datahandler.HadoopExampleSet;
import io.sugo.pio.spark.datahandler.mapreducehdfs.MapReduceHDFSHandler;
import io.sugo.pio.ports.OutputPort;

import java.util.Collection;

/**
 */
public abstract class SparkOperator extends Operator implements KillableOperation {
    private SparkNest sparkNest = null;

    public SparkOperator(String name, Collection<InputPort> inputPorts, Collection<OutputPort> outputPorts) {
        super(name, inputPorts, outputPorts);
    }

    public SparkNest getSparkNest() {
        if(sparkNest == null) {
            try {
                sparkNest = checkRadoopNest(this);
            } catch (Exception e) {
                return null;
            }
        }

        return sparkNest;
    }

    public MapReduceHDFSHandler getMapReduceHDFSHandler() {
        return getSparkNest().getMapReduceHDFSHandler();
    }

    private static SparkNest checkEnclosingRadoopNest(Operator operator)  {
        if(operator instanceof SparkNest) {
            return (SparkNest)operator;
        } else {
            OperatorChain parent = operator.getParent();
            if(parent == null) {
                return null;
            } else {
                while(true) {
                    String parentName = parent.getClass().getName();
                    if(parentName.compareTo(SparkNest.class.getName()) == 0) {
                        return (SparkNest)parent;
                    }

                    if(parent instanceof ProcessRootOperator) {
                        throw new RuntimeException("Nest not found");
                    }

                    parent = parent.getParent();
                }
            }
        }
    }

    public static SparkNest checkRadoopNest(Operator operator) {
        return checkEnclosingRadoopNest(operator);
    }

//    public OutputPort createOutputPort(String portName) {
//        return createOutputPort(portName, true);
//    }
//
//    public OutputPort createOutputPort(String portName, boolean add) {
//        SparkOutputPortImpl out = new SparkOutputPortImpl(getOutputPorts(), portName);
//        if(add) {
//            getOutputPorts().addPort(out);
//        }
//
//        return out;
//    }

    public HadoopExampleSet getHesFromInputPort(InputPort port) {
        IOObject exampleSet = port.getAnyDataOrNull();
        if (exampleSet instanceof HadoopExampleSet) {
            return (HadoopExampleSet)exampleSet;
        }

        throw new RuntimeException("Not an HadoopExampleSet, not acceptable.");
    }
}
