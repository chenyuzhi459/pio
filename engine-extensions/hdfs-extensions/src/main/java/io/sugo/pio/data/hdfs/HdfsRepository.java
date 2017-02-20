package io.sugo.pio.data.hdfs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.data.output.FSInputStream;
import io.sugo.pio.engine.data.output.Repository;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.permission.FsPermission;

import java.io.*;
import java.util.EnumSet;

/**
 */
public class HdfsRepository implements Repository {
    private final String path;
    private transient FileSystem fs;

    public HdfsRepository(String path, Configuration configuration) {
        this.path = path;
    }


    @JsonCreator
    public HdfsRepository(@JsonProperty("path") String path) {
        this(path, new Configuration());
    }

    @Override
    public OutputStream openOutput(String name) {
        try {
            FsServerDefaults fsDefaults = fs.getServerDefaults(new Path(path, name));
            EnumSet<CreateFlag> flags = EnumSet.of(CreateFlag.CREATE,
                    CreateFlag.OVERWRITE);
            return fs.create(new Path(path, name), FsPermission.getDefault(), flags, fsDefaults
                    .getFileBufferSize(), fsDefaults.getReplication(), fsDefaults
                    .getBlockSize(), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() {
        try {
            fs = FileSystem.newInstance(new Configuration());
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
                files[i] = fileStatuses[i].getPath().getName();
            }
            return files;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getSize(String name) {
        try {
            return fs.getContentSummary(new Path(path, name)).getLength();
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
            return new HdfsFileInputStream(fs.open(new Path(path, name)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isDistributed() {
        return true;
    }

    @JsonProperty
    public String getPath() {
        return path;
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
