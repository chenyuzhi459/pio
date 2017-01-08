package io.sugo.pio.spark.engine.data.output;

import java.io.IOException;

/**
 */
public interface Seekable {
    /**
     * Seek to the given offset from the start of the file.
     * The next read() will be from that location.  Can't
     * seek past the end of the file.
     */
    void seek(long pos) throws IOException;

    /**
     * Return the current offset from the start of the file
     */
    long getPos() throws IOException;
}
