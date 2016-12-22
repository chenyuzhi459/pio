package io.sugo.pio.spark.datahandler.mapreducehdfs;

import io.sugo.pio.spark.KillableOperation;
import io.sugo.pio.operator.Operator;

/**
 */
public class MapReduceHDFSHandler {
    public void runSpark(KillableOperation op, Operator operator, MapReduceHDFSHandler.SparkOperation sparkOp) {

    }

    public static enum SparkOperation {
        LogisticRegression("io.sugo.pio.spark.runner.SparkLogisticRegressionRunner"),
        TestCountJob("io.sugo.pio.spark.runner.SparkTestCountJobRunner"),
        DecisionTree("io.sugo.pio.spark.runner.SparkDecisionTreeRunner"),
        LinearRegression("io.sugo.pio.spark.runner.SparkLinearRegressionRunner"),
        DecisionTreeML("io.sugo.pio.spark.runner.SparkDecisionTreeMLRunner"),
        RandomForest("io.sugo.pio.spark.runner.SparkRandomForestRunner"),
        SupportVectorMachine("io.sugo.pio.spark.runner.SparkSupportVectorMachineRunner"),
        SparkScript_Python("org.apache.spark.deploy.PythonRunner"),
        SparkScript_R("org.apache.spark.deploy.RRunner"),
        SingleNodePushdown("io.sugo.pio.spark.runner.pushdown.SingleNodePushdownRunner"),
        MultiNodePushdown("io.sugo.pio.spark.runner.pushdown.MultiNodePushdownRunner"),
        GenerateData("io.sugo.pio.spark.runner.GenerateDataRunner");

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
