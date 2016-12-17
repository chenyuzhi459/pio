package sugo.io.pio.engine.template.data.output;

import sugo.io.pio.data.output.Repository;

import java.io.*;

/**
 */
public class LocalFileRepository implements Repository {
    String filename;

    public LocalFileRepository(String filename) {
        this.filename = filename;
    }

    @Override
    public OutputStream openOutput() {
        try {
            return new FileOutputStream(new File(filename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream openInput() {
        try {
            return new FileInputStream(new File(filename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
