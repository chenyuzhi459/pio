package sugo.io.pio.data.druid.format;

import com.metamx.common.Pair;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.joda.time.DateTime;
import sugo.io.pio.data.druid.directory.bytebuffer.ByteBufferDirectory;
import sugo.io.pio.data.druid.directory.hdfs.ZipHdfsDirectory;
import sugo.io.pio.data.druid.internal.DruidDirectoryReader;
import sugo.io.pio.data.druid.internal.FieldMappings;

import java.io.IOException;
import java.util.Map;

/**
 */
public class DruidRecordReader extends RecordReader<DateTime, Map> {
    private final FieldMappings fieldMappings;
    private final Directory directory;
    private final DruidDirectoryReader druidDirectoryReader;

    private DateTime key;
    private Map<String, Object> value;

    public DruidRecordReader(FileSystem fs, Path path) throws IOException {
        fieldMappings = FieldMappings.buildFrom(fs, path);
        directory = new ByteBufferDirectory(new ZipHdfsDirectory(fs, path), null);
        druidDirectoryReader = new DruidDirectoryReader(fieldMappings, directory);
    }

    @Override
    public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
    }

    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException {
        Pair<DateTime, Map<String, Object>> pair = druidDirectoryReader.read();
        if(null == pair.rhs) {
            value = null;
            return false;
        } else {
            key = pair.lhs;
            value = pair.rhs;
            return true;
        }
    }

    @Override
    public DateTime getCurrentKey() throws IOException, InterruptedException {
        return key;
    }

    @Override
    public Map<String, Object> getCurrentValue() throws IOException, InterruptedException {
        return value;
    }

    @Override
    public float getProgress() throws IOException, InterruptedException {
        return 0;
    }

    @Override
    public void close() throws IOException {

    }
}
