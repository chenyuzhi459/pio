package io.sugo.pio.engine.training;

import io.sugo.pio.engine.data.output.Repository;

/**
 */
public interface Model<MD> {
    void save(MD md, Repository repository);
}
