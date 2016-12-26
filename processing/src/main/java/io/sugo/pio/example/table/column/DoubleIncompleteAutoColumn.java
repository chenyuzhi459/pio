package io.sugo.pio.example.table.column;

/**
 * {@link Column} that stores double values in chunks. The chunks can either be sparse or dense and
 * switch automatically to the appropriate format for the given values. This column assumes that
 * complete is never called to indicate that the column is finished.
 *
 * @author Gisa Schaefer
 * @since 7.3.1
 */
final class DoubleIncompleteAutoColumn implements Column {

	private static final long serialVersionUID = 1L;

	private DoubleIncompleteAutoChunk[] chunks = new DoubleIncompleteAutoChunk[Integer.MAX_VALUE / AutoColumnUtils.CHUNK_SIZE
			+ 1];
	private int position = 0;
	private int chunkCount = 0;
	private int ensuredSize = 0;

	/**
	 * Constructs a column with enough chunks to fit size values.
	 *
	 * @param size
	 *            the size of the column
	 */
	DoubleIncompleteAutoColumn(int size) {
		ensure(size);
	}

	@Override
	public double get(int row) {
		return chunks[row >> AutoColumnUtils.CHUNK_SIZE_EXP].get(row & AutoColumnUtils.CHUNK_MODULO_MASK);
	}

	@Override
	public void set(int row, double value) {
		chunks[row >> AutoColumnUtils.CHUNK_SIZE_EXP].set(row & AutoColumnUtils.CHUNK_MODULO_MASK, value);
	}

	@Override
	public void append(double value) {
		set(position++, value);
	}

	@Override
	public void ensure(int size) {
		int completeChunks = 0;
		boolean enlargeLastChunk = false;
		if (chunkCount > 0) {
			if (ensuredSize % AutoColumnUtils.CHUNK_SIZE > 0) {
				completeChunks = chunkCount - 1;
				enlargeLastChunk = true;
			} else {
				completeChunks = chunkCount;
			}
		}

		int rowsLeft = size - completeChunks * AutoColumnUtils.CHUNK_SIZE;

		while (rowsLeft > 0) {
			int chunkSize = Math.min(rowsLeft, AutoColumnUtils.CHUNK_SIZE);
			if (enlargeLastChunk) {
				chunks[chunkCount - 1].ensure(chunkSize);
				enlargeLastChunk = false;
			} else {
				chunks[chunkCount] = new DoubleIncompleteDenseChunk(chunkCount, chunks, chunkSize);
				chunkCount++;
			}
			rowsLeft -= chunkSize;
		}

		ensuredSize = size;
	}

}
