package io.sugo.pio.data.hdfs;

import io.sugo.pio.engine.data.output.FSInputStream;
import io.sugo.pio.engine.data.output.Repository;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.*;

/**
 */
public class HdfsRepository implements Repository {
    private final String path;
    private final FileSystem fs;

    public HdfsRepository(String path) {
        this.path = path;
        try {
            this.fs = FileSystem.newInstance(new Configuration());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OutputStream openOutput(String name) {
        try {
            return fs.create(new Path(path, name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] listAll() {
        try {
            FileStatus[] fileStatuses = fs.listStatus(new Path(path));
            String[] files = new String[fileStatuses.length];
            for (int i=0;i<fileStatuses.length;i++) {
                files[i] = fileStatuses[i].getPath().toString();
            }
            return files;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getSize(String name) {
        try {
            return fs.getStatus(new Path(path, name)).getCapacity();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rename(String source, String dest) {
        try {
            fs.rename(new Path(path, source), new Path(path, dest));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(String name) {
        try {
            fs.create(new Path(path, name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(String name) {
        try {
            return fs.delete(new Path(path, name), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(String name) {
        try {
            return fs.exists(new Path(path, name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FSInputStream openInput(String name) {
        try {
            return new HdfsFileInputStream(FileSystem.newInstance(new Configuration()).open(new Path(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isDistributed() {
        return true;
    }

    static class HdfsFileInputStream extends FSInputStream {
        private FSDataInputStream fis;
        private long position;

        public HdfsFileInputStream(FSDataInputStream fis) throws IOException {
            this.fis = fis;
        }

        @Override
        public void seek(long pos) throws IOException {
            if (pos < 0) {
                throw new EOFException("Cannot seek to a negative offset");
            }
            fis.seek(pos);
            this.position = pos;
        }

        @Override
        public long getPos() throws IOException {
            return position;
        }

        @Override
        public int read() throws IOException {
            int value = fis.read();
            if (value >= 0) {
                this.position++;
            }
            return value;

        }

        @Override
        public void close() throws IOException {
            fis.close();
            super.close();
        }
    }
}
