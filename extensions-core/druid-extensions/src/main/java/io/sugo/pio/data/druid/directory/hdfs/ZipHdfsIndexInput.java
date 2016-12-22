
package io.sugo.pio.data.druid.directory.hdfs;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.lucene.store.IndexInput;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 */
public class ZipHdfsIndexInput extends IndexInput {
    private FileSystem fs;
    private Path path;
    private ZipInputStream stream;
    private ZipEntry entry;
    private long offset;
    long position;

    protected ZipHdfsIndexInput(ZipEntry entry, FileSystem fs, Path path) throws IOException {
        this(entry, fs, path, 0);
    }

    /**
     */
    protected ZipHdfsIndexInput(ZipEntry entry, FileSystem fs, Path path, long offset) throws IOException {
        super("");
        this.fs = fs;
        this.path = path;
        this.entry = entry;
        this.offset = offset;
        resetStream();
    }

    @Override
    public void close() throws IOException {
        position = 0;
        stream.close();
    }

    @Override
    public long getFilePointer() {
        return position;
    }

    @Override
    public void seek(long pos) throws IOException {
        if(pos < position) {
            // we need to start over because our inputstream doesn't have  a seek
            // we should probably use mark and reset...
            resetStream();
            stream.skip(pos);
            position = pos;
        }
        else {
            long togo = pos - position;
            stream.skip(togo);
            position = pos;
        }
    }

    @Override
    public long length() {
        return entry.getSize();
    }

    @Override
    public IndexInput slice(String sliceDescription, long offset, long length) throws IOException {
        return new SlicedIndexInput(entry, fs, path, offset, length);
    }

    @Override
    public byte readByte() throws IOException {
        byte b[] = new byte[1];
        if(stream.read(b) != 1) {
            throw new IOException();
        }
        position++;
        return b[0];
    }

    @Override
    public void readBytes(byte[] b, int offset, int len) throws IOException {
        int read = 0;
        while(read < len) {
            read += stream.read(b, offset+read, len - read);
        }
        position += read;
    }

    protected void resetStream()
            throws IOException {
        stream = new ZipInputStream(fs.open(path));
        ZipEntry tmpEntry;
        while(null != (tmpEntry = (stream.getNextEntry()))) {
            if (tmpEntry.getName().equals(entry.getName())) {
                break;
            }
        }
        position = 0;
        seek(offset);
    }


    @Override
    public ZipHdfsIndexInput clone() {
        ZipHdfsIndexInput clone = (ZipHdfsIndexInput)super.clone();

        clone.fs = fs;
        clone.path = path;
        clone.offset = offset;
        clone.entry = entry;

        try {
            clone.resetStream();
            clone.seek(position);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return clone;
    }

    public static class SlicedIndexInput extends ZipHdfsIndexInput {
        private final long length;

        private SlicedIndexInput(ZipEntry entry, FileSystem fs, Path path, long offset, long length) throws IOException {
            super(entry, fs, path, offset);
            this.length = length;
        }

        @Override
        public long length() {
            return length;
        }

    }

}
