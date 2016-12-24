package io.sugo.pio.spark.datahandler.mapreducehdfs;

import io.sugo.pio.operator.Operator;
import io.sugo.pio.spark.KillableOperation;
import io.sugo.pio.spark.HadoopTools;
import io.sugo.pio.spark.connections.HadoopConnectionEntry;
import io.sugo.pio.spark.datahandler.HadoopContext;
import io.sugo.pio.spark.operator.spark.SparkTools.SparkFinalState;
import io.sugo.pio.util.ParameterService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 */
public class MapReduceHDFSHandler {
    private final HadoopContext hadoopContext;
    private final HadoopConnectionEntry radoopConnection;
    private volatile String userDirectory = null;
    private final Object userDirectoryLock = new Object();
    private volatile String userName = null;
    private final Object userNameLock = new Object();

    public MapReduceHDFSHandler(HadoopContext hadoopContext) {
        this.hadoopContext = hadoopContext;
        this.radoopConnection = hadoopContext.getConnectionEntry();
    }

    public String getUsername() {
        if(userName == null) {
            synchronized(userNameLock) {
                if(userName == null) {
                     userName = HadoopTools.getNonEmptyUsername(radoopConnection);
                }
            }
        }

        return userName;
    }

    public String getUserDirectory() {
        if(userDirectory == null) {
            synchronized(userDirectoryLock) {
                if(userDirectory == null) {
                    userDirectory = getUserDirectory(getUsername());
                }
            }
        }

        return userDirectory;
    }

    private static String getUserDirectory(String username) {
        String userDirectory = getUserRootDirectory();
        userDirectory = userDirectory + username;
        if(!userDirectory.endsWith("/")) {
            userDirectory = userDirectory + "/";
        }

        return userDirectory;
    }

    public static String getUserRootDirectory() {
        String userRootDirectory = ParameterService.getParameterValue("rapidminer.radoop.hdfs_directory");
        if(!userRootDirectory.endsWith("/")) {
            userRootDirectory = userRootDirectory + "/";
        }

        return userRootDirectory;
    }

    public String[] getFileListAsStringArray(String dirName) throws IOException {
        FileSystem hdfs = FileSystem.get(getHadoopConfiguration());
        FileStatus[] fileStatuses = hdfs.globStatus(new Path(dirName + "*"));
        ArrayList directories = new ArrayList();

        for(int i = 0; i < fileStatuses.length; ++i) {
            if(fileStatuses[i].isDir()) {
                directories.add(dirName + fileStatuses[i].getPath().getName());
            }
        }

        return (String[])directories.toArray(new String[0]);
    }

    public HDFSInputStream getInputStream(String file) throws IOException {
        FileSystem hdfs = FileSystem.get(getHadoopConfiguration());
        return new HDFSInputStream(hdfs.open(new Path(file)));
    }

    private Configuration getHadoopConfiguration() {
        return hadoopContext.getHadoopConfiguration();
    }

    public SparkJobResult runSpark(KillableOperation op, Operator operator, MapReduceHDFSHandler.SparkOperation sparkOp) {
        return null;
    }

    public class HDFSDirectoryReader {
        private String[] fileList;
        private boolean initialized;
        private int index;
        private BufferedReader inputStream = null;

        public HDFSDirectoryReader(String dir) throws IOException {
            String[] fileListTmp = getFileListAsStringArray(dir);
            this.initialized = false;
            this.index = 0;
            this.fileList = new String[fileListTmp.length];
            int i = 0;
            String[] var5 = fileListTmp;
            int var6 = fileListTmp.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String s = var5[var7];
                this.fileList[i++] = s.substring(s.indexOf(dir));
            }

        }

        public String readLineFromDirectory() throws IOException {
            if(!initialized) {
                if(fileList.length == index) {
                    return null;
                }

                inputStream = new BufferedReader(new InputStreamReader(getInputStream(getUserDirectory() + fileList[index]), StandardCharsets.UTF_8));
                initialized = true;
            }

            String row;
            if((row = inputStream.readLine()) == null) {
                ++index;
                inputStream.close();
                initialized = false;
                return readLineFromDirectory();
            } else {
                return row;
            }
        }
    }

    public class HDFSInputStream extends InputStream {
        MapReduceHDFSHandler mrhdfsHandler = null;
        InputStream delegatedInputStream = null;

        public HDFSInputStream(InputStream inputStream) {
            this.mrhdfsHandler = MapReduceHDFSHandler.this;
            this.delegatedInputStream = inputStream;
        }

        public int read() throws IOException {
            try {
                return delegatedInputStream.read();
            } catch (IOException e) {
                throw e;
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        public int available() throws IOException {
            try {
                return delegatedInputStream.available();
            } catch (IOException e) {
                throw e;
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        public void close() throws IOException {
            try {
                delegatedInputStream.close();
            } catch (IOException e) {
                throw e;
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        public synchronized void mark(int readlimit) {
            try {
                delegatedInputStream.mark(readlimit);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        public boolean markSupported() {
            try {
                return delegatedInputStream.markSupported();
            } catch (Throwable var2) {
                throw new RuntimeException(var2);
            }
        }

        public int read(byte[] b) throws IOException {
            try {
                return delegatedInputStream.read(b);
            } catch (IOException e) {
                throw e;
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        public int read(byte[] b, int off, int len) throws IOException {
            try {
                return delegatedInputStream.read(b, off, len);
            } catch (IOException e) {
                throw e;
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        public synchronized void reset() throws IOException {
            try {
                delegatedInputStream.reset();
            } catch (IOException e) {
                throw e;
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        public long skip(long n) throws IOException {
            try {
                return delegatedInputStream.skip(n);
            } catch (IOException e) {
                throw e;
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    public static class SparkJobResult {
        private SparkFinalState finalState;
        private String applicationId;

        public SparkJobResult(String[] result) {
            if(result.length != 2) {
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
