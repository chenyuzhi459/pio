package io.sugo.pio.engine.common.lucene;

import org.apache.lucene.store.OutputStreamIndexOutput;

import java.io.OutputStream;

/**
 */
public class RepositoryWriter extends OutputStreamIndexOutput {
    public static final int BUFFER_SIZE = 16384;

    /**
     * Creates a new {@link OutputStreamIndexOutput} with the given buffer size.
     *
     * @param resourceDescription
     * @param out
     */
    public RepositoryWriter(String resourceDescription, OutputStream out) {
        super(resourceDescription, out, BUFFER_SIZE);
    }
}
