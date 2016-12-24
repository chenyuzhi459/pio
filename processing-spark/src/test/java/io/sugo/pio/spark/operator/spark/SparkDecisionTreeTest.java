package io.sugo.pio.spark.operator.spark;

import io.sugo.pio.Process;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorCreationException;
import io.sugo.pio.operator.OperatorDescription;
import io.sugo.pio.operator.ProcessRootOperator;
import io.sugo.pio.util.OperatorService;
import org.junit.Test;

/**
 */
public class SparkDecisionTreeTest {
    @Test
    public void test() throws OperatorCreationException {
        OperatorService.registerOperator(new OperatorDescription("root", ProcessRootOperator.class));
        Process process = new Process();
        SparkDecisionTree sparkDecisionTree = new SparkDecisionTree(new OperatorDescription("decisionTree", SparkDecisionTree.class));
        process.getRootOperator().getSubprocess(0).addOperator(sparkDecisionTree);
        process.getRootOperator().getSubprocess(0).autoWire(false, true);
        process.run();
    }
}
