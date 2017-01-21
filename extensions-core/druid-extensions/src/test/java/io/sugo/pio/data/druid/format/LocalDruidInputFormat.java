package io.sugo.pio.data.druid.format;

import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.*;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

/**
 */
public class LocalDruidInputFormat extends InputFormat<Long, Map> implements Configurable {

    public static final String DRUID_DATASOURCE = "druid.datasource";
    public static final String DRUID_PATH = "druid.path";
    public static final String DRUID_STARTTIME = "druid.starttime";
    public static final String DRUID_ENDTIME = "druid.endtime";

    private Configuration conf;
    private String datasource;
    private String path;
    private String startTime;
    private String untilTime;

    @Override
    public void setConf(Configuration conf) {
        this.conf = conf;

        this.datasource = conf.get(DRUID_DATASOURCE);
        this.path = conf.get(DRUID_PATH);;
        startTime = conf.get(DRUID_STARTTIME);
        untilTime = conf.get(DRUID_ENDTIME);
    }

    @Override
    public Configuration getConf() {
        return conf;
    }

    @Override
    public List<InputSplit> getSplits(JobContext context) throws IOException, InterruptedException {
        File rootDir = new File(path, datasource);
        List<File> dirs = recursiveListFiles(rootDir, 0);
        if(!dirs.isEmpty()) {
            List<InputSplit> splits = new ArrayList<>(dirs.size());
            for (File dir : dirs) {
                splits.add(new LocalDruidInputSplit(dir.getAbsolutePath()));
            }

            return splits;
        } else {
            return Collections.emptyList();
        }
    }

    private List<File> recursiveListFiles(File f, int depth)  {
        if (depth == 3) {
            return Arrays.asList(f.listFiles(new ZipFileFilter()));
        } else {
            File[] files = f.listFiles(new DirFilter());
            List<File> res = new ArrayList<>();
            for (File file : files) {
                res.addAll(recursiveListFiles(file, depth+1));
            }

            return res;
        }
    }

    @Override
    public RecordReader<Long, Map> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
        LocalDruidInputSplit druidSplit = (LocalDruidInputSplit)split;
        return new LocalDruidRecordReader(druidSplit.getPath());
    }

    private static class ZipFileFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return "index.zip".equals(name);
        }
    }

    private static class DirFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    }
}
