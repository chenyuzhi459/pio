package io.sugo.pio.spark.engine;

import io.sugo.pio.spark.engine.data.output.Repository;

/**
 */
public interface Model<MD> {
    void save(MD md, Repository repository);

    MD read(Repository repository);
}
