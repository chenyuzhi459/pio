package io.sugo.pio.engine.common.data;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;

/**
 * Created by penghuan on 2017/4/26.
 */
public class HdfsDataWriter implements DataWriter<Path, String> {
    private String hdfsPath;
    private FileSystem hdfs;
//    private DistributedFileSystem hdfs;
    private BufferedWriter bufferedWriter = null;

    public HdfsDataWriter(String coreSiteFile, String hdfsPath) {
        this.hdfsPath = hdfsPath;
        Configuration conf = new Configuration();
        conf.addResource(new Path(coreSiteFile));
        try {
//            hdfs = FileSystem.get(conf);
            hdfs = DistributedFileSystem.get(conf);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    class PathIterator implements Iterator<Path> {
        private Integer fileId = 0;
        private Path rootPath;

        public PathIterator() {
            rootPath = new Path(hdfsPath);
            try {
                if (!hdfs.exists(rootPath)) {
                    hdfs.mkdirs(rootPath);
                } else if (hdfs.isDirectory(rootPath)) {
                    hdfs.delete(rootPath, true);
                    hdfs.mkdirs(rootPath);
                } else {
                    hdfs.delete(rootPath, false);
                    hdfs.createNewFile(rootPath);
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }

        @Override
        public boolean hasNext() {
            try {
                return (fileId == 0 || hdfs.isDirectory(rootPath));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }

        @Override
        public Path next() {
            try {
                if (hdfs.isFile(rootPath)) {
                    return rootPath;
                } else {
                    return new Path(hdfsPath + String.format("/%04d", fileId++));
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Iterator<Path> getRepositoryIterator() {
        return new PathIterator();
    }

    @Override
    public void write(Path path, String data) {
        try {
            if (bufferedWriter == null) {
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(hdfs.create(path, true)));
            }
            bufferedWriter.write(data);
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void flush() {
        if (bufferedWriter != null) {
            try {
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public void close() {
        if (bufferedWriter != null) {
            try {
                bufferedWriter.close();
                bufferedWriter = null;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
