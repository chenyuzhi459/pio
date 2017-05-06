package io.sugo.pio.engine.common.data;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Iterator;

/**
 * Created by penghuan on 2017/4/26.
 */
public class LocalDataWriter implements DataWriter<File, String> {
    private String filePath;
    private BufferedWriter bufferedWriter = null;

    public LocalDataWriter(String filePath) {
        this.filePath = filePath;
    }

    class FileIterator implements Iterator<File> {
        private Integer fileId = 0;
        private File rootFile;

        public FileIterator() {
            rootFile = new File(filePath);
            try {
                if (!rootFile.exists()) {
                    if (!rootFile.mkdir()) {
                        throw new IOException(String.format("Create directory failed: %s", filePath));
                    }
                } else if (rootFile.isDirectory()) {
                    FileUtils.deleteDirectory(rootFile);
                    if (!rootFile.mkdir()) {
                        throw new IOException(String.format("Create directory failed: %s", filePath));
                    }
                } else if (rootFile.isFile()) {
                    FileUtils.forceDelete(rootFile);
                    if (!rootFile.createNewFile()) {
                        throw new IOException(String.format("Create file fialed: %s", filePath));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }

        @Override
        public boolean hasNext() {
            return (fileId == 0 || rootFile.isDirectory());
        }

        @Override
        public File next() {
            if (rootFile.isFile()) {
                return rootFile;
            } else {
                return new File(rootFile.getAbsolutePath() + String.format("/%04d", fileId++));
            }
        }
    }

    @Override
    public Iterator<File> getRepositoryIterator() {
        return new FileIterator();
    }

    @Override
    public void write(File file, String data) {
        try {
            if (bufferedWriter == null) {
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false)));
            }
            bufferedWriter.write(data);
            bufferedWriter.newLine();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void flush() {
        if (bufferedWriter != null) {
            try {
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public void close() {
        if (bufferedWriter != null) {
            try {
                bufferedWriter.close();
                bufferedWriter = null;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
