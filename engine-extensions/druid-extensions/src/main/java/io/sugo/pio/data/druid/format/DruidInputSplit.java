package io.sugo.pio.data.druid.format;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.InputSplit;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 */
public class DruidInputSplit extends InputSplit implements Writable {
    private String uriPath;
    private Path path;

    public DruidInputSplit() {
    }

    public DruidInputSplit(String uriPath) {
        this.uriPath = uriPath;
        path = new Path(uriPath);
    }

    @Override
    public long getLength() throws IOException, InterruptedException {
        return 0;
    }

    @Override
    public String[] getLocations() throws IOException, InterruptedException {
        return new String[0];
    }

    @Override
    public void write(DataOutput out) throws IOException {
        Text.writeString(out, uriPath);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        uriPath = Text.readString(in);
        path = new Path(uriPath);
    }

    public Path getPath() {
        return path;
    }
}
