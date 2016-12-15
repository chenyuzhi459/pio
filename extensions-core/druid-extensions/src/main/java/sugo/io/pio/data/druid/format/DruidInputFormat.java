package sugo.io.pio.data.druid.format;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapreduce.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */
public class DruidInputFormat extends InputFormat<DateTime, Map> implements Configurable {
    public static final String DRUID_DATASOURCE = "druid.datasource";
    public static final String DRUID_COORDINATOR_URL = "druid.coordinator.url";
    public static final String DRUID_STARTTIME = "druid.starttime";
    public static final String DRUID_ENDTIME = "druid.endtime";

    private final HttpClient httpclient;
    private final ObjectMapper objectMapper;

    private Configuration conf;
    private String datasource;
    private String coordinatorUrl;
    private Interval interval;

    public DruidInputFormat() {
        httpclient = new DefaultHttpClient();
        objectMapper = new ObjectMapper();
    }

    @Override
    public List<InputSplit> getSplits(JobContext context) throws IOException, InterruptedException {
        HttpGet getRequest = new HttpGet(getUrl());
        HttpResponse response = httpclient.execute(getRequest);
        List<Map<String, Object>> segments = objectMapper.readValue(response.getEntity().getContent(), new TypeReference<List<Map<String, Object>>>()
        {

        });
        List<InputSplit> splits = new ArrayList<>(segments.size());
        Map<String, String> loadSpec;
        String path;
        for (Map<String, Object> segment : segments) {
            Map<String, Map<String, String>> map = (Map<String, Map<String, String>>)segment.get("segment");
            loadSpec = map.get("loadSpec");
            if (null != (path = loadSpec.get("path"))) {
                splits.add(new DruidInputSplit(path));
            }
        }

        return splits;
    }

    private String getUrl() {
        return String.format("http://%s/druid/coordinator/v1/datasources/%s/intervals/%s/serverview?partial=true",
                coordinatorUrl,
                datasource,
                interval.toString().replace("/", "_")
            );
    }

    @Override
    public RecordReader<DateTime, Map> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
        DruidInputSplit druidSplit = (DruidInputSplit)split;
        Configuration conf = new Configuration();
        return new DruidRecordReader(FileSystem.get(conf), druidSplit.getPath());
    }

    @Override
    public void setConf(Configuration conf) {
        this.conf = conf;

        this.datasource = conf.get(DRUID_DATASOURCE);
        this.coordinatorUrl = conf.get(DRUID_COORDINATOR_URL);
        DateTime startTime = new DateTime(conf.get(DRUID_STARTTIME));
        DateTime untilTime = new DateTime(conf.get(DRUID_ENDTIME));
        interval = new Interval(startTime, untilTime);
    }

    @Override
    public Configuration getConf() {
        return conf;
    }

}
