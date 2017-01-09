package io.sugo.pio.spark.engine.data.output;


import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 */
public abstract class FSInputStream extends InputStream
        implements Seekable, PositionedReadable {

    @Override
    public int read(long position, byte[] buffer, int offset, int length) throws IOException {
        synchronized (this) {
            long oldPos = getPos();
            int nread = -1;
            try {
                seek(position);
                nread = read(buffer, offset, length);
            } finally {
                seek(oldPos);
            }
            return nread;
        }
    }

    @Override
    public void readFully(long position, byte[] buffer, int offset, int length) throws IOException {
        int nread = 0;
        while (nread < length) {
            int nbytes = read(position+nread, buffer, offset+nread, length-nread);
            if (nbytes < 0) {
                throw new EOFException("End of file reached before reading fully.");
            }
            nread += nbytes;
        }
    }

    @Override
    public void readFully(long position, byte[] buffer) throws IOException {
        readFully(position, buffer, 0, buffer.length);
    }
}
