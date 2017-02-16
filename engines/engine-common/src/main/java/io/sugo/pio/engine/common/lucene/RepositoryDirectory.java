package io.sugo.pio.engine.common.lucene;

import io.sugo.pio.engine.data.output.Repository;
import org.apache.lucene.store.*;

import java.io.IOException;
import java.util.Collection;

/**
 */
public class RepositoryDirectory extends BaseDirectory {
    private Repository repository;

    public RepositoryDirectory(Repository repository) {
        super(RepositoryLockFactory.INSTANCE);
        this.repository = repository;
        repository.init();
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

}
