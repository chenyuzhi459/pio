package io.sugo.pio.spark.operator.spark;

import io.sugo.pio.Process;
import io.sugo.pio.operator.ExecutionUnit;
import io.sugo.pio.operator.OperatorCreationException;
import io.sugo.pio.operator.OperatorDescription;
import io.sugo.pio.operator.ProcessRootOperator;
import io.sugo.pio.spark.SparkNest;
import io.sugo.pio.util.OperatorService;
import org.junit.Test;

import java.util.List;

/**
 */
public class SparkDecisionTreeTest {
    @Test
    public void test() throws OperatorCreationException {
//        OperatorService.registerOperator(new OperatorDescription("root", ProcessRootOperator.class));
//        List<ExecutionUnit> execUnits;
//        Process process = new Process("name", new ProcessRootOperator(execUnits));
//        TestHadoopExampleSetReader exampleSetReader = new TestHadoopExampleSetReader(new OperatorDescription("reader", TestHadoopExampleSetReader.class));
//        SparkDecisionTree sparkDecisionTree = new SparkDecisionTree(new OperatorDescription("decisionTree", SparkDecisionTree.class));
//
//        SparkNest sparkNest = new SparkNest(new OperatorDescription("sparkNest", SparkNest.class));
//        sparkNest.getSubprocess(0).addOperator(exampleSetReader);
//        sparkNest.getSubprocess(0).addOperator(sparkDecisionTree);
//        sparkNest.getSubprocess(0).autoWire(false, true);
//
//        process.getRootOperator().getSubprocess(0).addOperator(sparkNest);
//        process.getRootOperator().getSubprocess(0).autoWire(false, true);
//        process.run();
    }
}
