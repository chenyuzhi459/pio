package io.sugo.pio.spark.operator.spark;

import io.sugo.pio.Process;
import io.sugo.pio.operator.*;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.impl.InputPortImpl;
import io.sugo.pio.ports.impl.OutputPortImpl;
import io.sugo.pio.spark.SparkNest;
import io.sugo.pio.util.OperatorService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class SparkDecisionTreeTest {
    @Test
    public void test() throws OperatorCreationException {
        OperatorService.registerOperator(new OperatorDescription("root", ProcessRootOperator.class));

        OutputPort outputPort = new OutputPortImpl("hadoopOutput");
        InputPort inputPort = new InputPortImpl("hadoopInput");
        OutputPort modelOutputPort = new OutputPortImpl("modelOutput");
        outputPort.connectTo(inputPort);
        TestHadoopExampleSetReader exampleSetReader = new TestHadoopExampleSetReader(outputPort);
        SparkDecisionTree sparkDecisionTree = new SparkDecisionTree("tree", inputPort, modelOutputPort);

        List<ExecutionUnit> sparkNestExecUnits = new ArrayList<>();
        List<Operator> operators = new ArrayList<>();
        operators.add(exampleSetReader);
        operators.add(sparkDecisionTree);
        ExecutionUnit sparkNestUnit = new ExecutionUnit(operators);
        sparkNestExecUnits.add(sparkNestUnit);

        SparkNest sparkNest = new SparkNest(sparkNestExecUnits);

        List<Operator> rootOperators = new ArrayList<>();
        rootOperators.add(sparkNest);
        ExecutionUnit rootExecUnit = new ExecutionUnit(rootOperators);
        List<ExecutionUnit> rootExecUnits = new ArrayList<>();
        rootExecUnits.add(rootExecUnit);
        Process process = new Process("name", new ProcessRootOperator(rootExecUnits));
        process.run();
    }
}
