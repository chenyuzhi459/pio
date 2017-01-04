package io.sugo.pio.spark.datahandler.mapreducehdfs;

import io.sugo.pio.spark.KillableOperation;
import io.sugo.pio.spark.SparkConfig;
import io.sugo.pio.spark.SparkVersion;
import io.sugo.pio.spark.connections.HadoopConnectionEntry;
import io.sugo.pio.spark.operator.spark.SparkTools.SparkFinalState;
import io.sugo.pio.spark.transfer.parameter.SparkParameter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.spark.deploy.yarn.Client;

/**
 */
public class MapReduceHDFSHandler {
    private final HadoopConnectionEntry hadoopConnection;
    private final SparkConfig sparkConfig;

    public MapReduceHDFSHandler(HadoopConnectionEntry hadoopConnection, SparkConfig sparkConfig) {
        this.hadoopConnection = hadoopConnection;
        this.sparkConfig = sparkConfig;
    }

    public String getUserDirectory() {
        return "";
    }

    private Configuration getHadoopConfiguration() {
        return hadoopConnection.getConfiguration();
    }

    public SparkJobResult runSpark(KillableOperation op,
                                   MapReduceHDFSHandler.SparkOperation sparkOp,
                                   SparkParameter commonParams,
                                   SparkParameter params) {
        String[] result = runSpark(sparkConfig.getSparkVersion(),
                sparkConfig.getSparkAssemblyJar(),
                sparkOp.getSparkClassName(),
                op.getName(),
                commonParams == null? null: commonParams.toJson(),
                params == null? null: params.toJson());
        return new MapReduceHDFSHandler.SparkJobResult(result);
    }

    private String[] runSpark(String sparkVersionString,
                              String sparkAssemblyJar,
                              String sparkClassName,
                              String appName,
                              String commonParams,
                              String params) {
        SparkVersion sparkVersion = SparkVersion.getFromId(sparkVersionString);
        SparkSubmissionHandler ssh = SparkSubmissionHandler.createSubmissionHandler(sparkVersion);
        ssh.initConf(appName, sparkClassName);
        ssh.setSparkLibsPath(sparkAssemblyJar, false);
        ssh.setUserArguments(commonParams, params);
        Client client = new Client(ssh.createClientArguments(), getHadoopConfiguration(), ssh.getSparkConf());
        ApplicationId appId = client.submitApplication();
        return new String[]{monitorSparkApplication(client, appId), appId.toString()};
    }

    private String monitorSparkApplication(Client c, ApplicationId appId) {
        short interval = 5000;
        YarnApplicationState state;
        do {
            try {
                Thread.sleep((long)interval);
            } catch (InterruptedException var9) {
                ;
            }

            ApplicationReport report = c.getApplicationReport(appId);
            state = report.getYarnApplicationState();
        } while(state != YarnApplicationState.FINISHED && state != YarnApplicationState.FAILED && state != YarnApplicationState.KILLED);
        return c.getApplicationReport(appId).getFinalApplicationStatus().name();
    }

    public static class SparkJobResult {
        private SparkFinalState finalState;
        private String applicationId;

        public SparkJobResult(String[] result) {
            if (result.length != 2) {
                throw new IllegalArgumentException("Failed to parse Spark results.");
            } else {
                finalState = SparkFinalState.valueOf(result[0]);
                applicationId = result[1];
            }
        }

        public SparkFinalState getFinalState() {
            return finalState;
        }

        public String getApplicationId() {
            return applicationId;
        }
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
        GenerateData("io.sugo.pio.spark.runner.GenerateDataRunner"),
        CustomEngine("io.sugo.pio.spark.runner.SparkCustomEngineRunner");

        private String sparkClassName;

        private SparkOperation(String sparkClassName) {
            this.sparkClassName = sparkClassName;
        }

        public String getSparkClassName() {
            return sparkClassName;
        }

    }
}
