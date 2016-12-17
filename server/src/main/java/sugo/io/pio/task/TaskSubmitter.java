package sugo.io.pio.task;


import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.yarn.Client;
import org.apache.spark.deploy.yarn.ClientArguments;
import sugo.io.pio.deploy.SubmitArgs;
import sugo.io.pio.engine.EngineInstance;
import sugo.io.pio.metadata.SparkConfig;
import sugo.io.pio.server.EngineStorage;
import sugo.io.pio.task.training.BatchTrainingTask;
import java.io.*;
import java.util.*;
import sugo.io.pio.util.ZkUtils;
import com.google.common.base.Optional;
/**
 */
public class TaskSubmitter {
    //yarn-site  zk
    private static final String RESOURCEMANAGER_ZK_ADDRESS= "yarn.resourcemanager.zk-address";
    //yarn-site  resourcemanager
    private static final String RESOURCEMANAGER_ADDRESS ="yarn.resourcemanager.address";
    private static final String RESOURCEMANAGER_HOSTNAME ="yarn.resourcemanager.hostname";
    //yarn-site  resourcemanager scheduler
    private static final String RESOURCEMANAGER_SCHEDULER_ADDRESS ="yarn.resourcemanager.scheduler.address";
    private static final String RESOURCEMANAGER_SCHEDULER_HOSTNAME ="yarn.resourcemanager.scheduler.hostname";
    //zk active resourcemanager node
    private static final String RESOURCEMANAGER_ACTIVE_PATH= "/yarn-leader-election/sugo-rm/ActiveStandbyElectorLock";

    private static ZkUtils zkUtils = null;

    public static String submit(ClusterType clusterType, Task task, EngineStorage engineStorage, SparkConfig sparkConfig)
            throws IOException, InterruptedException {

        if(task ==null || engineStorage == null || sparkConfig ==null){
            return null;
        }
        BatchTrainingTask btTask = null;
        if(task instanceof BatchTrainingTask){
            btTask = (BatchTrainingTask) task;
        }
        String enginId = btTask.getId();
        Optional<EngineInstance> oe = engineStorage.get(enginId);
        if(oe.equals(Optional.absent())){
            return null;
        }
        EngineInstance engineInstance = oe.get();
        String location = engineInstance.getLocation();
        if(location == null ){
            return null;
        }
        String []  arr = location.split(";");
        if(arr.length != 2){
            return null;
        }

        String userJar = sparkConfig.getUserJar();
        String mainClass = sparkConfig.getMainClass();
        // "/home/kitty/WorkSpace/intellij/Sugo-pio/testjar/target/testjar.jar";
        // "hdfs://sugo/user/kitty/share/lib/testjar.jar";
        String customJar = arr[0];
        // "com.sugo.io.pio.test.testjar.UREngin";
        String customEngin = arr[1];
        if(userJar.isEmpty() || mainClass.isEmpty() || customJar.isEmpty() || customEngin.isEmpty() ){
            return null;
        }
        List<String> args = new LinkedList<>();
        //"/home/kitty/WorkSpace/intellij/Sugo-pio/engintest/target/engintest-1.0-SNAPSHOT-jar-with-dependencies.jar";
        //"hdfs://sugo/user/kitty/share/lib/engintest-1.0-SNAPSHOT-jar-with-dependencies.jar";
        StringBuilder addJars = new StringBuilder();
        addJars.append(customJar);
        //主参数
        Map<String,String> primaryArgs = new LinkedHashMap<>();
        primaryArgs.put(SubmitArgs.MAIN_CLASS,mainClass);
        primaryArgs.put(SubmitArgs.USER_JAR,userJar);
        //业务参数
        List<String> businessArgs = new ArrayList<>();
        businessArgs.add("enginClassPath="+customEngin);

        //spark参数
        Map<String,String> sparkArgs = new HashMap();
        //spark环境有关参数
        Map<String,Object> envArgs = sparkConfig.getEnv();
        //sparkApp有关参数
        Map<String,Object> appArgs = sparkConfig.getApp();
        if(envArgs != null){
            for(Map.Entry<String,Object> envArg :envArgs.entrySet()){
                sparkArgs.put(envArg.getKey(), (String) envArg.getValue());
            }
        }
        if(appArgs != null){
            for(Map.Entry<String,Object> appArg :appArgs.entrySet()){
                sparkArgs.put(appArg.getKey(), (String) appArg.getValue());
            }
        }
        sparkArgs.put(SubmitArgs.SPARK_YARN_DIST_JARS,addJars.toString());
        //load primaryArgs
        for(Map.Entry primaryArg:primaryArgs.entrySet()){
            args.add((String) primaryArg.getKey());
            args.add((String) primaryArg.getValue());
        }

        //load businessArgs
        for(String businessArg : businessArgs){
            args.add(SubmitArgs.ARG);
            args.add(businessArg);
        }

        if(ClusterType.YARN == clusterType){
            System.setProperty(SubmitArgs.SPARK_YARN_MODE, Boolean.TRUE.toString());
            //need cluster model
            sparkArgs.put(SubmitArgs.DEPLOY_MODE,"cluster");
        }

        Configuration config = new Configuration();
        config.addResource(TaskSubmitter.class.getResourceAsStream("/hadoop/core-site.xml"));
        config.addResource(TaskSubmitter.class.getResourceAsStream("/hadoop/hdfs-site.xml"));
        config.addResource(TaskSubmitter.class.getResourceAsStream("/hadoop/mapred.xml"));
        config.addResource(TaskSubmitter.class.getResourceAsStream("/hadoop/yarn-site.xml"));

        //Caused by: org.apache.zookeeper.KeeperException$ConnectionLossException: KeeperErrorCode = ConnectionLoss for /yarn-leader-election/sugo-rm/ActiveStandbyElectorLock
        zkUtils = ZkUtils.getInstance(config.get(RESOURCEMANAGER_ZK_ADDRESS));
        String zkData = zkUtils.getData(RESOURCEMANAGER_ACTIVE_PATH);
        String activeRM  = zkData.replace("\n","").replaceAll("[^0-9a-zA-Z]","");
        activeRM = activeRM.substring(6,activeRM.length());
        config.set(RESOURCEMANAGER_ADDRESS, config.get(RESOURCEMANAGER_ADDRESS+"."+activeRM));
        config.set(RESOURCEMANAGER_SCHEDULER_ADDRESS, config.get(RESOURCEMANAGER_SCHEDULER_ADDRESS+"."+activeRM));

        SparkConf sparkConf = new SparkConf();
        for(Map.Entry sparkArg:sparkArgs.entrySet()){
            sparkConf.set((String)sparkArg.getKey(), (String) sparkArg.getValue());
        }

        String [] argsArr = new String[args.size()];
        args.toArray(argsArr);
        ClientArguments cArgs = new ClientArguments(argsArr);
        Client client = new Client(cArgs,config,sparkConf);
        try {
            //client.run();
            ApplicationId appId = client.submitApplication();
            return appId.toString();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static void loadResource(String resourcePath,Map<String,String> sysProps,String sep) {
        InputStream in = TaskSubmitter.class.getResourceAsStream(resourcePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        try {
            while((line = reader.readLine())!=null){
                String [] kv = line.split(sep);
                if(kv.length==2){
                    sysProps.put(kv[0],kv[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
