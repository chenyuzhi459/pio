package io.sugo.pio.data.druid.directory.zip;

import org.apache.lucene.store.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 */
public class ZipDirectory extends BaseDirectory {
    private final String[] files;
    private final ZipFile file;

    /**
     * Sole constructor.
     */
    public ZipDirectory(String path) throws IOException  {
        this(path, new SingleInstanceLockFactory());
    }

    public ZipDirectory(String path, LockFactory lockFactory) throws IOException {
        super(lockFactory);
        List<String> list = new ArrayList<>();
        file = new ZipFile(path);
        Enumeration entries = file.entries();
        while(entries.hasMoreElements()) {
            ZipEntry e = (ZipEntry)entries.nextElement();
            list.add(e.getName());
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
        return file.getEntry(name).getSize();
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
        return new ZipIndexInput(name, file);
    }

    @Override
    public void close() throws IOException {
    }
}
