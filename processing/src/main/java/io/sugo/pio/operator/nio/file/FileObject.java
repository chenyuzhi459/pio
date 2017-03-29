package io.sugo.pio.operator.nio.file;

import io.sugo.pio.operator.Annotations;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.ResultObjectAdapter;
import io.sugo.pio.tools.Tools;

import java.io.*;


/**
 *
 * This class represents buffers, files or streams that can be parsed by Operators.
 *
 * @author Nils Woehler, Marius Helf
 *
 */
public abstract class FileObject extends ResultObjectAdapter {

	private static final long serialVersionUID = 1L;

	/**
	 * Open Stream to read data in this Object.
	 *
	 * @throws OperatorException
	 */
	public abstract InputStream openStream() throws OperatorException;

	/**
	 * Returns the data as a file. Maybe slow if underlying implementation needs to copy the data
	 * into the file first. This file should be used only for reading. Writing to this file has an
	 * undefined effect.
	 *
	 * @throws OperatorException
	 */
	public abstract File getFile() throws OperatorException;

	/**
	 * Returns the size of the related file in number of bytes.
	 *
	 * @throws OperatorException
	 */
	public abstract long getLength() throws OperatorException;

	@Override
	public String getName() {
		return "File";
	}

	/**
	 * Returns the filename of this file object.<br/>
	 * <br/>
	 * If the {@link Annotations#KEY_FILENAME} annotation is defined, returns it. Otherwise, tries
	 * to retrieve the {@link Annotations#KEY_SOURCE} annotation and extract a filename from it.<br/>
	 * <br/>
	 * Returns null if none of the above works.
	 */
	public String getFilename() {
		String filename = getAnnotations().getAnnotation(Annotations.KEY_FILENAME);
		if (filename == null) {
			filename = getAnnotations().getAnnotation(Annotations.KEY_SOURCE);
			if (filename != null) {
				filename = filename.replaceAll(".*[/\\\\]([^/\\\\\\?]*).*", "$1");
			}
		}
		return filename;
	}

	protected Object writeReplace() throws ObjectStreamException {
		if (this instanceof BufferedFileObject) {
			return this;
		}
		InputStream fileInputStream = null;
		try {
			fileInputStream = openStream();
			return new BufferedFileObject(Tools.readInputStream(fileInputStream));
		} catch (OperatorException e) {
			throw new WriteAbortedException("Could not write FileObject", e);
		} catch (IOException e) {
			throw new WriteAbortedException("Could not write FileObject", e);
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
