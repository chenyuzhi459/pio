package io.sugo.pio.repository;

import java.io.InputStream;
import java.io.OutputStream;


/**
 * A byte blob with no specified contents.
 * 
 * */
public interface BlobEntry extends DataEntry {

	public static final String TYPE_NAME = "blob";

	/**
	 * Opens a stream to read from this entry.
	 * 
	 * @throws RepositoryException
	 */
	public InputStream openInputStream() throws RepositoryException;

	/**
	 * Opens a stream to this blob, setting its mime type to the given value.
	 * 
	 * @return TODO
	 */
	public OutputStream openOutputStream(String mimeType) throws RepositoryException;

	public String getMimeType();

}
