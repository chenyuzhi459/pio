package sugo.io.pio.data.druid.directory.hdfs;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.lucene.store.*;

import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 */
public class ZipHdfsDirectory extends BaseDirectory {
    private final FileSystem fs;
    private final FSDataInputStream fsin;
    private final ZipInputStream zip;
    private final Map<String, ZipEntry> zipEntries;
    private final String[] files;
    private final Path path;

    /**
     * Sole constructor.
     */
    public ZipHdfsDirectory(FileSystem fs, Path path) throws IOException  {
        this(path, new SingleInstanceLockFactory(), fs);
    }

    public ZipHdfsDirectory(Path path, LockFactory lockFactory, FileSystem fs) throws IOException {
        super(lockFactory);
        this.path = path;
        this.fs = fs;
        fsin = this.fs.open(path);
        zip = new ZipInputStream(fsin);
        ZipEntry entry;
        List<String> list = new ArrayList<>();
        zipEntries = new HashMap<>();
        while(null != (entry = (zip.getNextEntry()))) {
            list.add(entry.getName());
            zipEntries.put(entry.getName(), entry);
        }
        files = new String[list.size()];
        list.toArray(files);
    }

    @Override
    public String[] listAll() throws IOException {
        return files;
    }

    @Override
    public void deleteFile(String name) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long fileLength(String name) throws IOException {
        ZipEntry entry = zipEntries.get(name);
        if (null == entry) {
            throw  new RuntimeException("");
        }
        return entry.getSize();
    }

    @Override
    public IndexOutput createOutput(String name, IOContext context) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sync(Collection<String> names) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void renameFile(String source, String dest) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IndexInput openInput(String name, IOContext context) throws IOException {
        return new ZipHdfsIndexInput(zipEntries.get(name), fs, path);
    }

    @Override
    public void close() throws IOException {
    }
}
