package io.sugo.pio.spark.datahandler.mapreducehdfs;

import io.sugo.pio.spark.KillableOperation;
import io.sugo.pio.spark.SparkConfig;
import io.sugo.pio.spark.SparkVersion;
import io.sugo.pio.spark.connections.HadoopConnectionEntry;
import io.sugo.pio.spark.operator.spark.SparkTools.SparkFinalState;
import io.sugo.pio.spark.transfer.parameter.SparkParameter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.spark.deploy.yarn.Client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
                                   List<String> jarDependencyPaths,
                                   SparkParameter commonParams,
                                   SparkParameter params) {
        String[] result = runSpark(
                sparkOp.getSparkClassName(),
                op.getName(),
                jarDependencyPaths,
                commonParams == null ? null : commonParams.toJson(),
                params == null ? null : params.toJson());
        return new MapReduceHDFSHandler.SparkJobResult(result);
    }

    private String[] runSpark(String sparkClassName,
                              String appName,
                              List<String> jarDependencyPaths,
                              String commonParams,
                              String params) {
        SparkVersion sparkVersion = SparkVersion.getFromId(sparkConfig.getSparkVersion());
        SparkSubmissionHandler ssh = SparkSubmissionHandler.createSubmissionHandler(sparkVersion);
        ssh.initConf(appName, sparkClassName);
        ssh.setYarnQueue(sparkConfig.getYarnQueue());
        ssh.setSparkLibsPath(sparkConfig.getSparkAssemblyJar(), false);
        ssh.setUserJar(sparkConfig.getCommonJarLocation());
        ssh.setAdditionalJars(jarDependencyPaths);
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
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                ;
            }

            ApplicationReport report = c.getApplicationReport(appId);
            state = report.getYarnApplicationState();
        }
        while (state != YarnApplicationState.FINISHED && state != YarnApplicationState.FAILED && state != YarnApplicationState.KILLED);
        return c.getApplicationReport(appId).getFinalApplicationStatus().name();
    }

    public void checkHadoopLibOnHDFS(String localFilePath, String hdfsLocation, String hdfsFileName) {
    }

    private boolean checkHdfsFile(String localFilePath, String hdfsLocation, String hdfsFileName) throws IOException {
        String hdfsFilePath = hdfsLocation + "/" + hdfsFileName;
        FileSystem hdfs = FileSystem.get(getHadoopConfiguration());
        Path f = new Path(hdfsFilePath);
        Long hdfsFileSize = hdfs.exists(f) && hdfs.isFile(f) ? Long.valueOf(hdfs.getFileStatus(f).getLen()) : null;

        if(hdfsFileSize == null) {
            return false;
        } else if(hdfsFileSize == 0L) {
            return false;
        } else {
            long localSize = -1L;
            try {
                localSize = Files.size(Paths.get(localFilePath, new String[0]));
            } catch (IOException e) {

            }

            if(localSize != hdfsFileSize.longValue()) {
                return false;
            }

            return true;
        }
    }

    private void uploadJar(File jarFile, Path path) throws IOException
    {
        FileSystem fs = FileSystem.get(getHadoopConfiguration());
        com.google.common.io.Files.asByteSource(jarFile).copyTo(fs.create(path));
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

    public enum SparkOperation {
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
