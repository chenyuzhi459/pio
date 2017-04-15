package io.sugo.pio.operator.nio.file;

import io.sugo.pio.operator.OperatorException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


/**
 * Simple implementation of a {@link FileObject} backed by a {@link File}.
 *
 * @author Nils Woehler
 *
 */
public class SimpleFileObject extends FileObject {

	private static final long serialVersionUID = 1L;

	private File file;

	public SimpleFileObject(File file) {
		super();
		this.file = file;
	}

	@Override
	public InputStream openStream() throws OperatorException {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new OperatorException("301", e, file);
		}
	}

	@Override
	public File getFile() {
		return file;
	}

	/**
	 * Returns the size of the related file in number of bytes. Returns 0, if the file does not
	 * exist.
	 */
	@Override
	public long getLength() throws OperatorException {
		return file.length();
	}

	@Override
	public String toString() {
		return "File: " + getFile().getAbsolutePath();
	}

}
