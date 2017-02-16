package io.sugo.pio.engine.common.lucene;

import io.sugo.pio.engine.data.output.FSInputStream;
import io.sugo.pio.engine.data.output.Repository;
import org.apache.lucene.store.BufferedIndexInput;

import java.io.IOException;

/**
 */
public class RepositoryInput extends BufferedIndexInput {
    private final long length;
    private FSInputStream inputStream;
    private boolean clone = false;

    public RepositoryInput(String name, Repository repository) {
        super(name);
        this.inputStream = repository.openInput(name);
        this.length = repository.getSize(name);
    }

    @Override
    public RepositoryInput clone() {
        RepositoryInput clone = (RepositoryInput) super.clone();
        clone.clone = true;
        return clone;
    }

    @Override
    protected void readInternal(byte[] b, int offset, int length) throws IOException {
        inputStream.readFully(getFilePointer(), b, offset, length);
    }

    @Override
    protected void seekInternal(long pos) throws IOException {
    }

    @Override
    public void close() throws IOException {
        if (!clone) {
            inputStream.close();
        }
    }

    @Override
    public long length() {
        return length;
    }
}
