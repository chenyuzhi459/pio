package io.sugo.pio.engine.common.data;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by penghuan on 2017/4/26.
 */
public class HdfsDataReader implements DataReader<Path, String> {
    private String hdfsPath;
    private FileSystem hdfs;

    public HdfsDataReader(String coreSiteFile, String hdfsPath) {
        this.hdfsPath = hdfsPath;
        Configuration conf = new Configuration();
        conf.addResource(new Path(coreSiteFile));
        try {
            hdfs = FileSystem.get(conf);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    class PathIterator implements Iterator<Path> {
        private Iterator<Path> pathIterator;

        public PathIterator(String pathStr) {
            Path path = new Path(pathStr);
            List<Path> pathList = new ArrayList<>();
            try {
                if (hdfs.isFile(path)) {
                    pathList.add(path);
                } else {
                    RemoteIterator<LocatedFileStatus> fileStatusListIterator = hdfs.listFiles(path, false);
                    while (fileStatusListIterator.hasNext()) {
                        pathList.add(fileStatusListIterator.next().getPath());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
            pathIterator = pathList.iterator();
        }

        @Override
        public boolean hasNext() {
            return pathIterator.hasNext();
        }

        @Override
        public Path next() {
            return pathIterator.next();
        }
    }

    class StringIterator implements Iterator<String> {
        private BufferedReader bufferedReader;
        private String line = null;

        StringIterator(Path path) {
            try {
                this.bufferedReader = new BufferedReader(new InputStreamReader(hdfs.open(path)));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }

        @Override
        public boolean hasNext() {
            line = null;
            try {
                line = bufferedReader.readLine();
                if (line == null) {
                    bufferedReader.close();
                    return false;
                } else {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }

        @Override
        public String next() {
            return line;
        }
    }

    @Override
    public Iterator<Path> getRepositoryIterator() {
        return new PathIterator(hdfsPath);
    }

    @Override
    public Iterator<String> getDataIterator(Path path) {
        return new StringIterator(path);
    }

    @Override
    public void close() {
        try {
            hdfs.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
