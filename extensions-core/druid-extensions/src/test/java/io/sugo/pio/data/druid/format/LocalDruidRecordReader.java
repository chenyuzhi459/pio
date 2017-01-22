package io.sugo.pio.data.druid.format;

import io.sugo.pio.data.druid.directory.bytebuffer.ByteBufferDirectory;
import io.sugo.pio.data.druid.directory.zip.ZipDirectory;
import io.sugo.pio.data.druid.internal.DruidDirectoryReader;
import io.sugo.pio.data.druid.internal.FieldMappings;
import io.sugo.pio.data.druid.tools.Pair;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.Map;

/**
 */
public class LocalDruidRecordReader extends RecordReader<Long, Map> {
    private final FieldMappings fieldMappings;
    private final Directory directory;
    private final DruidDirectoryReader druidDirectoryReader;

    private Long key;
    private Map<String, Object> value;

    public LocalDruidRecordReader(String path) throws IOException {
        fieldMappings = FieldMappings.buildFrom(path);
        directory = new ByteBufferDirectory(new ZipDirectory(path), null);
        druidDirectoryReader = new DruidDirectoryReader(fieldMappings, directory);
    }

    @Override
    public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
    }

    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException {
        Pair<Long, Map<String, Object>> pair = druidDirectoryReader.read();
        if(null == pair.getSecond()) {
            value = null;
            return false;
        } else {
            key = pair.getFirst();
            value = pair.getSecond();
            return true;
        }
    }

    @Override
    public Long getCurrentKey() throws IOException, InterruptedException {
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
