package io.sugo.pio.data.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 */
public class HdfsRepositoryTest {
    private static MiniDFSCluster miniCluster;
    private static File hdfsTmpDir;
    private static Configuration conf;

    @BeforeClass
    public static void setupStatic() throws IOException, ClassNotFoundException
    {
        hdfsTmpDir = File.createTempFile("hdfsHandlerTest", "dir");
        hdfsTmpDir.deleteOnExit();
        if (!hdfsTmpDir.delete()) {
            throw new IOException(String.format("Unable to delete hdfsTmpDir [%s]", hdfsTmpDir.getAbsolutePath()));
        }
        conf = new Configuration(true);
        conf.set(MiniDFSCluster.HDFS_MINIDFS_BASEDIR, hdfsTmpDir.getAbsolutePath());
        miniCluster = new MiniDFSCluster.Builder(conf).build();
    }

    @AfterClass
    public static void tearDownStatic() throws IOException
    {
        if (miniCluster != null) {
            miniCluster.shutdown(true);
        }
    }

    @Test
    public void testWriteFile() {
        HdfsRepository repository = new HdfsRepository("/", conf);
        String testFile = "test.txt";
        repository.create(testFile);
        String[] files = repository.listAll();
        assert null != files && files.length == 1;
    }
}