package io.sugo.pio.example.table.column;

import java.io.Serializable;


/**
 * {@link Column} that stores integer values in chunks. The chunks can either be sparse or dense and
 * switch automatically to the appropriate format for the given values. This column assumes that
 * complete is never called to indicate that the column is finished.
 *
 * @author Gisa Schaefer
 * @since 7.3.1
 */
final class IntegerIncompleteAutoColumn implements Column {

	private static final long serialVersionUID = 1L;

	/**
	 * Building block of a {@link IntegerIncompleteAutoColumn}.
	 *
	 * @author Gisa Schaefer
	 *
	 */
	static abstract class IntegerIncompleteChunk implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * the position of this chunk in {@link IntegerIncompleteAutoColumn#chunks}
		 */
		final int id;

		/**
		 * the chunk array {@link IntegerIncompleteAutoColumn#chunks}
		 */
		final IntegerIncompleteChunk[] chunks;

		IntegerIncompleteChunk(int id, IntegerIncompleteChunk[] chunks) {
			this.id = id;
			this.chunks = chunks;
		}

		/**
		 * Ensures that the internal data structure can hold up to {@code size} values.
		 *
		 * @param size
		 *            the size that should be ensured
		 */
		abstract void ensure(int size);

		/**
		 * Gets the value at the specified row.
		 *
		 * @param row
		 *            the row that should be looked up
		 * @return the value at the specified row
		 */
		abstract double get(int row);

		/**
		 * Sets the value at the specified row to the given value.
		 *
		 * @param row
		 *            the row that should be set
		 * @param value
		 *            the value that should be set at the row
		 */
		abstract void set(int row, double value);

	}

	private IntegerIncompleteChunk[] chunks = new IntegerIncompleteChunk[Integer.MAX_VALUE / AutoColumnUtils.CHUNK_SIZE + 1];
	private int position = 0;
	private int chunkCount = 0;
	private int ensuredSize = 0;

	/**
	 * Constructs a column with enough chunks to fit size values.
	 *
	 * @param size
	 *            the size of the column
	 */
	IntegerIncompleteAutoColumn(int size) {
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
//				chunks[chunkCount] = new IntegerIncompleteDenseChunk(chunkCount, chunks, chunkSize);
				chunkCount++;
			}
			rowsLeft -= chunkSize;
		}

		ensuredSize = size;
	}

}
