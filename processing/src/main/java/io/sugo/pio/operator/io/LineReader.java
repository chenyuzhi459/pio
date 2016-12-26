package io.sugo.pio.operator.io;

import java.io.*;
import java.nio.charset.Charset;


/**
 * A helper class for reading line based data formats
 *
 * @author Tobias Malbrecht
 */
public class LineReader implements AutoCloseable {

	private BufferedReader reader = null;
	private FileInputStream fis = null;

	public LineReader(File file) throws FileNotFoundException {
		fis = new FileInputStream(file);
		reader = new BufferedReader(new InputStreamReader(fis));
	}

	public LineReader(File file, Charset encoding) throws FileNotFoundException {
		fis = new FileInputStream(file);
		reader = new BufferedReader(new InputStreamReader(fis, encoding));
	}

	public LineReader(InputStream stream, Charset encoding) {
		if (stream instanceof FileInputStream) {
			fis = (FileInputStream) stream;
		}
		reader = new BufferedReader(new InputStreamReader(stream, encoding));
	}

	public String readLine() throws IOException {
		return reader.readLine();
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	/**
	 * @return If the LineReader reads from a FileInputStream, the size of the FileChannel is
	 *         returned. Returns -1 otherwise.
	 *
	 * @throws IOException
	 */
	public long getSize() throws IOException {
		if (fis != null) {
			return fis.getChannel().size();
		}
		return -1L;
	}

	/**
	 * @return If the LineReader reads from a FileInputStream, the position of the FileChannel is
	 *         returned. Returns -1 otherwise.
	 */
	public long getPosition() throws IOException {
		if (fis != null) {
			return fis.getChannel().position();
		}
		return -1L;
	}
}
