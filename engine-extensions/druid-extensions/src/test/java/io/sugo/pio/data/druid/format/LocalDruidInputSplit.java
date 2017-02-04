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
public class LocalDruidInputSplit extends InputSplit implements Writable {
    private String path;

    public LocalDruidInputSplit() {
    }

    public LocalDruidInputSplit(String path) {
        this.path = path;
    }


    @Override
    public void write(DataOutput out) throws IOException {
        Text.writeString(out, path);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        path = Text.readString(in);
    }

    @Override
    public long getLength() throws IOException, InterruptedException {
        return 0;
    }

    @Override
    public String[] getLocations() throws IOException, InterruptedException {
        return new String[0];
    }

    public String getPath() {
        return path;
    }
}
