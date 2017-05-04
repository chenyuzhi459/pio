package io.sugo.pio.engine.common.data;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by penghuan on 2017/4/26.
 */
public class LocalDataReader implements DataReader<File, String> {
    private String filePath;

    public LocalDataReader(String filePath) {
        this.filePath = filePath;
    }

    class FileIterator implements Iterator<File> {
        private Iterator<File> fileIterator;

        public FileIterator(String path) {
            File file = new File(path);
            List<File> fileList = new ArrayList<>();
            if (file.isFile()) {
                fileList.add(file);
            } else {
                for (File f : file.listFiles()) {
                    if (f.isFile()) {
                        fileList.add(f);
                    }
                }
            }
            fileIterator = fileList.iterator();
        }

        @Override
        public boolean hasNext() {
            return fileIterator.hasNext();
        }

        @Override
        public File next() {
            return fileIterator.next();
        }
    }

    class StringIterator implements Iterator<String> {
        private BufferedReader bufferedReader;
        private String line = null;

        StringIterator(File file) {
            try {
                this.bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }

        @Override
        public boolean hasNext() {
            line = null;
            try {
                line = bufferedReader.readLine();
                if (line == null) {
                    bufferedReader.close();
                    return false;
                } else {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }

        @Override
        public String next() {
            return line;
        }
    }

    @Override
    public Iterator<File> getRepositoryIterator() {
        return new FileIterator(filePath);
    }

    @Override
    public Iterator<String> getDataIterator(File file) {
        return new StringIterator(file);
    }

    @Override
    public void close() {
        ;
    }
}
