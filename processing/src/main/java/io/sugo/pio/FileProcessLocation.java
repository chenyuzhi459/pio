package io.sugo.pio;

import io.sugo.pio.tools.ProgressListener;

import java.io.File;
import java.io.IOException;


/**
 * @author Simon Fischer
 */
public class FileProcessLocation implements ProcessLocation {

    private final File file;

    public FileProcessLocation(File file) {
        this.file = file;
    }

    @Override
    public OperatorProcess load(ProgressListener l) throws IOException {
        if (!file.exists()) {
            throw new IOException("Process file '" + file + "' does not exist.");
        }
        if (!file.canRead()) {
            throw new IOException("Process file '" + file + "' is not readable.");
        }
        return new OperatorProcess("", null);
    }

    @Override
    public String toHistoryFileString() {
        return "file " + file.getAbsolutePath();
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toMenuString() {
        return file.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FileProcessLocation)) {
            return false;
        } else {
            return ((FileProcessLocation) o).file.equals(this.file);
        }
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }

    @Override
    public String toString() {
        return file.toString();
    }

    @Override
    public String getShortName() {
        return file.getName();
    }
}
