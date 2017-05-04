package io.sugo.pio.engine.common.spark;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.metamx.common.logger.Logger;
import io.sugo.pio.common.utils.HttpClientUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by penghuan on 2017/4/24.
 */
public class SparkJobServer {
    private static final Logger logger = new Logger(SparkJobServer.class);

    private final String host;
    private final Integer port;
    private final Integer connectTimeout = 60;
    private final Integer readTimeout = 600;
    private final static String SUCCESS = "SUCCESS";

    public enum JobStatus {
        Running("RUNNING"),
        Finished("FINISHED"),
        Error("ERROR");

        private String status;
        JobStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return status;
        }
    }

    public SparkJobServer(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    static class ConextInfo {
        public String status;
        public String result;
    }

    static class JobCreateInfo {
        public String duration;
        public String classPath;
        public String startTime;
        public String context;
        public String status;
        public String jobId;
    }

    static class JobStatusInfo {
        public String duration;
        public String classPath;
        public String startTime;
        public String context;
        public Object result;
        public String status;
        public String jobId;
    }

    private List<String> listContext() {
        String url = String.format("http://%s:%d/contexts", host, port);
        logger.info(url);
        try {
            String result = HttpClientUtil.get(
                    url,
                    HttpClientUtil.clientWithTimeout(connectTimeout, readTimeout)
            );
            return JSON.parseArray(result, String.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean isContextExists(String context) {
        List<String> contextList = listContext();
        return contextList.contains(context);
    }

    public boolean createContext(String context) {
        String url = String.format("http://%s:%d/contexts/%s?num-cpu-cores=%d&memory-per-node=%s",
                host, port, context, 1, "512m");
        logger.info(url);
        try {
            if (isContextExists(context)) {
                return true;
            }
            String result = HttpClientUtil.post(
                    url,
                    HttpClientUtil.clientWithTimeout(connectTimeout, readTimeout),
                    HttpClientUtil.requestBody("", "text/plain; charset=utf-8")
            );
            ConextInfo info = JSON.parseObject(result, ConextInfo.class);
            if (info.status.equals(SUCCESS)) {
                return true;
            } else {
                logger.error(info.result);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean createJavaContext(String context) {
        String url = String.format("http://%s:%d/contexts/%s?context-factory=spark.jobserver.context.JavaSparkContextFactory&num-cpu-cores=%d&memory-per-node=%s",
                host, port, context, 1, "512m");
        logger.info(url);
        try {
            if (isContextExists(context)) {
                return true;
            }
            String result = HttpClientUtil.post(
                    url,
                    HttpClientUtil.clientWithTimeout(connectTimeout, readTimeout),
                    HttpClientUtil.requestBody("", "text/plain; charset=utf-8")
            );
            ConextInfo info = JSON.parseObject(result, ConextInfo.class);
            if (info.status.equals(SUCCESS)) {
                return true;
            } else {
                logger.error(info.result);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean deleteContext(String context) {
        String url = String.format("http://%s:%d/contexts/%s", host, port, context);
        logger.info(url);
        try {
            if (!isContextExists(context)) {
                return true;
            }
            String result = HttpClientUtil.delete(
                    url,
                    HttpClientUtil.clientWithTimeout(connectTimeout, readTimeout)
            );
            ConextInfo info = JSON.parseObject(result, ConextInfo.class);
            if (info.status.equals(SUCCESS)) {
                return true;
            } else {
                logger.error(info.result);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private List<String> listAppName() {
        String url = String.format("http://%s:%d/binaries", host, port);
        logger.info(url);
        try {
            String result = HttpClientUtil.get(
                    url,
                    HttpClientUtil.clientWithTimeout(connectTimeout, readTimeout)
            );
            Map<String, Object> info = JSON.parseObject(result, new TypeReference<HashMap<String, Object>>() {});
            return new ArrayList<String>(info.keySet());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean isAppNameExists(String appName) {
        List<String> appNameList = listAppName();
        return appNameList.contains(appName);
    }

    public boolean addBinary(String appName, String binaryPath) {
        String url = String.format("http://%s:%d/binaries/%s", host, port, appName);
        logger.info(url);
        try {
            String result = HttpClientUtil.post(
                    url,
                    HttpClientUtil.clientWithTimeout(connectTimeout, readTimeout),
                    HttpClientUtil.requestBody(new File(binaryPath), "application/java-archive")
            );
            return result.equals("OK");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean deleteBinary(String appName) {
        String url = String.format("http://%s:%d/binaries/%s", host, port, appName);
        try {
            String result = HttpClientUtil.delete(
                    url,
                    HttpClientUtil.clientWithTimeout(connectTimeout, readTimeout)
            );
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String getJob(String jobId) {
        String url = String.format("http://%s:%d/jobs/%s", host, port, jobId);
        logger.info(url);
        try {
            String result = HttpClientUtil.get(
                    url,
                    HttpClientUtil.clientWithTimeout(connectTimeout, readTimeout)
            );
            JobStatusInfo info = JSON.parseObject(result, JobStatusInfo.class);
            return info.status;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void deleteJob(String jobId) {
        String url = String.format("http://%s:%d/jobs/%s", host, port, jobId);
        logger.info(url);
        try {
            String result = HttpClientUtil.delete(
                    url,
                    HttpClientUtil.clientWithTimeout(connectTimeout, readTimeout)
            );
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String createJob(String context, String appName, String classPath, String args) {
        String url = String.format("http://%s:%d/jobs?context=%s&appName=%s&classPath=%s&sync=false",
                host, port, context, appName, classPath);
        logger.info(url);
        try {
            String result = HttpClientUtil.post(
                    url,
                    HttpClientUtil.clientWithTimeout(connectTimeout, readTimeout),
                    HttpClientUtil.requestBody(args, "text/plain; charset=utf-8")
            );
            JobCreateInfo info = JSON.parseObject(result, JobCreateInfo.class);
            return info.jobId;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String host = "localhost";
        Integer port = 8090;
        String context = "test-context";
//        String appName = "test";
        String appName = "sugo-test";
        String binaryPath = "/Users/penghuan/Code/sugo/sugo-pio/engines/user-feature-extraction-engine-new/target/user-feature-extraction-engine-new-1.0-SNAPSHOT-jar-with-dependencies.jar";
//        String binaryPath = "/Users/penghuan/Code/test/java/target/test-slf4j-1.0-jar-with-dependencies.jar";
        String classPath = "io.sugo.pio.engine.userFeatureExtractionNew.main.UserFeatureExtractionSparkJobServer";
//        String classPath = "sanhuan.test.spark.jobserver.TestJobServer";
        SparkJobServer sparkJobServer = new SparkJobServer(host, port);

        //        System.out.println(sparkJobServer.listContext());
//        System.out.println(sparkJobServer.deleteContext(context));
        System.out.println(sparkJobServer.createJavaContext(context));

//        System.out.println(sparkJobServer.listAppName());
//        System.out.println(sparkJobServer.deleteBinary(appName));
//        System.out.println(sparkJobServer.addBinary(appName, binaryPath));

//        String jobId = sparkJobServer.createJob(context, appName, classPath, "");
//        System.out.println(sparkJobServer.getJob(jobId));
//        sparkJobServer.deleteJob("844000bc-ac2f-491b-a567-5b721092de66");
    }
}
