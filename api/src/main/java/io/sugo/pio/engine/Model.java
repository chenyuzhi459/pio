package io.sugo.pio.engine;

import io.sugo.pio.data.output.Repository;

/**
 */
public interface Model<MD> {
    void save(MD md, Repository repository);

    MD read(Repository repository);
}
