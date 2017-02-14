
package io.sugo.pio.data.druid.directory.zip;

import org.apache.lucene.store.IndexInput;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 */
public class ZipIndexInput extends IndexInput {
    private String name;
    private ZipEntry entry;
    private ZipFile file;
    private long offset;
    private InputStream stream;
    private long position;

    protected ZipIndexInput(String name, ZipFile file) throws IOException {
        this(name, file, 0);
    }

    /**
     */
    protected ZipIndexInput(String name, ZipFile file, long offset) throws IOException {
        super("");
        this.name = name;
        this.offset = offset;
        this.file = file;

        entry = file.getEntry(name);
        if(entry == null) {
            throw new IOException("No entry with name " + name);
        }
        resetStream();
    }

    @Override
    public void close() throws IOException {
        position = 0;
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
        return new SlicedIndexInput(name, file, offset, length);
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
        stream = file.getInputStream(entry);
        position = 0;
        seek(offset);
    }


    @Override
    public ZipIndexInput clone() {
        ZipIndexInput clone = (ZipIndexInput)super.clone();
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

    public static class SlicedIndexInput extends ZipIndexInput {
        private final long length;

        private SlicedIndexInput(String name, ZipFile file, long offset, long length) throws IOException {
            super(name, file, offset);
            this.length = length;
        }

        @Override
        public long length() {
            return length;
        }

    }

}
