package sugo.io.pio.engine;

import sugo.io.pio.data.output.Repository;

/**
 */
public interface Model<MD> {
    void save(MD md, Repository repository);

    MD read(Repository repository);
}
