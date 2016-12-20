package io.sugo.pio.data.druid.directory.bytebuffer;


import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ByteBufferFile {
    private static final long serialVersionUID = 1l;

    protected ArrayList buffers = new ArrayList();

    long length;
    ByteBufferDirectory directory;
    protected long sizeInBytes;

    // File used as buffer, in no RAMDirectory
    protected ByteBufferFile() {

    }

    ByteBufferFile(ByteBufferDirectory directory) {
        this.directory = directory;
    }

    // For non-stream access from thread that might be concurrent with writing
    public long getLength() {
        return length;
    }

    protected void setLength(long length) {
        this.length = length;
    }

    protected final ByteBuffer addBuffer(int size) {
        //byte[] buffer = newBuffer(size);
        ByteBuffer buffer = ByteBuffer.allocateDirect(size);
        buffers.add(buffer);
        sizeInBytes += size;

        if (directory != null) {
            directory.sizeInBytes += size;
        }
        return buffer;
    }

    protected ByteBuffer getBuffer(int index) {
        return (ByteBuffer) buffers.get(index);
    }

    protected int numBuffers() {
        return buffers.size();
    }
}
