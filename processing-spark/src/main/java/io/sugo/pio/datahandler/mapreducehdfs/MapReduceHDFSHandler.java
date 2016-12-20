package io.sugo.pio.datahandler.mapreducehdfs;

import io.sugo.pio.KillableOperation;
import io.sugo.pio.operator.Operator;

/**
 */
public class MapReduceHDFSHandler {
    public void runSpark(KillableOperation op, Operator operator, MapReduceHDFSHandler.SparkOperation sparkOp) {

    }

    public static enum SparkOperation {
        LogisticRegression("io.sugo.pio.runner.SparkLogisticRegressionRunner"),
        TestCountJob("io.sugo.pio.runner.SparkTestCountJobRunner"),
        DecisionTree("io.sugo.pio.runner.SparkDecisionTreeRunner"),
        LinearRegression("io.sugo.pio.runner.SparkLinearRegressionRunner"),
        DecisionTreeML("io.sugo.pio.runner.SparkDecisionTreeMLRunner"),
        RandomForest("io.sugo.pio.runner.SparkRandomForestRunner"),
        SupportVectorMachine("io.sugo.pio.runner.SparkSupportVectorMachineRunner"),
        SparkScript_Python("org.apache.spark.deploy.PythonRunner"),
        SparkScript_R("org.apache.spark.deploy.RRunner"),
        SingleNodePushdown("io.sugo.pio.runner.pushdown.SingleNodePushdownRunner"),
        MultiNodePushdown("io.sugo.pio.runner.pushdown.MultiNodePushdownRunner"),
        GenerateData("io.sugo.pio.runner.GenerateDataRunner");

        private String sparkClassName;

        private SparkOperation(String sparkClassName) {
            this.sparkClassName = sparkClassName;
        }

        public String getSparkClassName() {
            return this.sparkClassName;
        }

        public boolean isPushdown() {
            return this == SingleNodePushdown || this == MultiNodePushdown;
        }
    }
}
