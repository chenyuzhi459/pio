package sugo.io.pio.engine;

/**
 */
public interface Model<MD> {
    void save(MD md);

    MD read();
}
