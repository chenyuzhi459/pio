package io.sugo.pio.engine.data.output;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 */
public class LocalFileRepository implements Repository {
    private String path;

    public LocalFileRepository(String path) {
        this.path = path;
        try {
            Files.createDirectories(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OutputStream openOutput(String name) {
        try {
            return new FileOutputStream(new File(path, name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] listAll() {
        String[] files = new File(path).list();
        if (null == files) {
            return new String[0];
        }
        return files;
    }

    @Override
    public long getSize(String name) {
        try {
            return Files.size(Paths.get(path, name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rename(String source, String dest) {
        new File(path, source).renameTo(new File(path, dest));
    }

    @Override
    public void create(String name) {
        try {
            Files.createFile(Paths.get(path, name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(String name) {
        try {
            return Files.deleteIfExists(Paths.get(path, name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(String name) {
        return Files.exists(Paths.get(path, name));
    }

    @Override
    public FSInputStream openInput(String name) {
        try {
            return new LocalFSFileInputStream(new File(path, name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class LocalFSFileInputStream extends FSInputStream {
        private FileInputStream fis;
        private long position;

        public LocalFSFileInputStream(File f) throws IOException {
            fis = new FileInputStream(f);
        }

        @Override
        public void seek(long pos) throws IOException {
            if (pos < 0) {
                throw new EOFException("Cannot seek to a negative offset");
            }
            fis.getChannel().position(pos);
            this.position = pos;
        }

        @Override
        public long getPos() throws IOException {
            return position;
        }

        @Override
        public int read() throws IOException {
            int value = fis.read();
            if (value >= 0) {
                this.position++;
            }
            return value;

        }

        @Override
        public void close() throws IOException {
            fis.close();
            super.close();
        }
    }
}
