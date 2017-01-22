package io.sugo.pio.spark.datahandler.mapreducehdfs;

import io.sugo.pio.spark.KillableOperation;
import io.sugo.pio.spark.SparkConfig;
import io.sugo.pio.spark.connections.HadoopConnectionEntry;
import io.sugo.pio.spark.connections.service.HadoopConnectionService;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
public class MapReduceHDFSHandlerTest {
    private SparkConfig sparkConfig;
    private MapReduceHDFSHandler handler;

//    @Before
//    public void setUp()
//    {
//        sparkConfig = new SparkConfig();
//        sparkConfig.setSparkAssemblyJar("hdfs://sugo/user/root/share/lib/spark-2.0.0.zip");
//        List<String> hadoopConfigFiles = new ArrayList<>();
//        hadoopConfigFiles.add(this.getClass().getClassLoader().getResource("conf/hadoop/core-site.xml").getPath());
//        hadoopConfigFiles.add(this.getClass().getClassLoader().getResource("conf/hadoop/hdfs-site.xml").getPath());
//        hadoopConfigFiles.add(this.getClass().getClassLoader().getResource("conf/hadoop/mapred-site.xml").getPath());
//        hadoopConfigFiles.add(this.getClass().getClassLoader().getResource("conf/hadoop/yarn-site.xml").getPath());
//
//        sparkConfig.setHadoopConfigFiles(hadoopConfigFiles);
//        sparkConfig.setCommonJarLocation("/home/yaotc/IdeaProjects/pio-sugo/processing-spark-common/target/pio-processing-spark-common-1.0-SNAPSHOT.jar");
//        sparkConfig.setWorkDirectory("hdfs://sugo/user/yaotc/");
//        HadoopConnectionEntry hadoopConnection = HadoopConnectionService.getConnectionEntry(sparkConfig);
//        handler = new MapReduceHDFSHandler(hadoopConnection, sparkConfig);
//    }
//
//    @Test
//    public void test() {
//        handler.runSpark(new KillableOperation() {
//            @Override
//            public String getName() {
//                return "test";
//            }
//        }, MapReduceHDFSHandler.SparkOperation.TestCountJob, Collections.emptyList(), null, null);
//    }
}
