package io.sugo.pio.engine.common.lucene;

import io.sugo.pio.spark.engine.data.output.FSInputStream;
import io.sugo.pio.spark.engine.data.output.Repository;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.lucene.store.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 */
public class RepositoryDirectory extends BaseDirectory {
    private Repository repository;

    public RepositoryDirectory(Repository repository) {
        super(RepositoryLockFactory.INSTANCE);
        this.repository = repository;
    }

    @Override
    public String[] listAll() throws IOException {
        return repository.listAll();
    }

    @Override
    public void deleteFile(String name) throws IOException {
        repository.delete(name);
    }

    @Override
    public long fileLength(String name) throws IOException {
        return repository.getSize(name);
    }

    @Override
    public IndexOutput createOutput(String name, IOContext context) throws IOException {
        return new RepositoryWriter("name="+name, repository.openOutput(name));
    }

    @Override
    public void sync(Collection<String> names) throws IOException {
    }

    @Override
    public void renameFile(String source, String dest) throws IOException {
        repository.rename(source, dest);
    }

    @Override
    public IndexInput openInput(String name, IOContext context) throws IOException {
        return new RepositoryInput(name, repository);
    }

    public Repository getRepository() {
        return repository;
    }

    @Override
    public void close() throws IOException {
        this.isOpen = false;
    }

    static class RepositoryInput extends CustomBufferedIndexInput {
        private final FSInputStream inputStream;
        private final long length;
        private boolean clone = false;

        public RepositoryInput(String name, Repository repository) {
            super(name);
            this.inputStream = repository.openInput(name);
            this.length = repository.getSize(name);
        }

        @Override
        protected void closeInternal() throws IOException {
            if (!clone) {
                inputStream.close();
            }
        }

        @Override
        protected void readInternal(byte[] b, int offset, int length) throws IOException {
            inputStream.readFully(getFilePointer(), b, offset, length);
        }

        @Override
        protected void seekInternal(long pos) throws IOException {
            inputStream.seek(pos);
        }

        @Override
        public long length() {
            return length;
        }
    }
}
