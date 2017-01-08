package io.sugo.pio.spark.engine;

import io.sugo.pio.spark.engine.data.output.Repository;

/**
 */
public interface Model<MD, SD> {
    void save(MD md, Repository repository);

    SD load(Repository repository);
}
