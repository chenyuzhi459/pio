package io.sugo.pio.engine.common.lucene;

import io.sugo.pio.engine.data.output.Repository;
import org.apache.lucene.store.*;

import java.io.IOException;

/**
 */
public class RepositoryLockFactory extends LockFactory {
    public static final RepositoryLockFactory INSTANCE = new RepositoryLockFactory();

    private RepositoryLockFactory() {
    }

    @Override
    public Lock obtainLock(Directory dir, String lockName) throws IOException {
        RepositoryDirectory repositoryDirectory = (RepositoryDirectory) dir;

        while (true) {
            try {
                Repository repository = repositoryDirectory.getRepository();
                repository.create(lockName);
                return new RepositoryLock(repository, lockName);
            } catch (Exception e) {
                throw new LockObtainFailedException("Cannot obtain lock lockName: " + lockName, e);
            }
        }
    }

    private static final class RepositoryLock extends Lock {
        private final Repository repository;
        private final String lockName;
        private volatile boolean closed;

        RepositoryLock(Repository repository, String lockName) {
            this.repository = repository;
            this.lockName = lockName;
        }

        public void close() throws IOException {
            if (!closed) {
                if (repository.exists(lockName) && !repository.delete(lockName)) {
                    throw new LockReleaseFailedException("failed to delete: " + lockName);
                }
            }
        }

        public void ensureValid() throws IOException {
        }
    }
}
