package sugo.io.pio.data.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import sugo.io.pio.data.output.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 */
public class HdfsRepository implements Repository {
    private String path;

    public HdfsRepository(String path) {
        this.path = path;
    }

    @Override
    public OutputStream openOutput() {
        try {
            return FileSystem.newInstance(new Configuration()).create(new Path(path));
        } catch (IOException e) {
        }
        return null;
    }

    @Override
    public InputStream openInput() {
        try {
            return FileSystem.newInstance(new Configuration()).open(new Path(path));
        } catch (IOException e) {
        }
        return null;
    }
}
