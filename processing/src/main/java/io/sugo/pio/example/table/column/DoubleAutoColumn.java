package io.sugo.pio.example.table.column;

import java.io.Serializable;


/**
 * {@link Column} that stores double values in chunks. The chunks can either be sparse or dense and
 * switch automatically to the appropriate format for the given values. For this column,
 * {@link #complete()} must be called when the size is final before {@link #get(int)} or
 * {@link #set(int, double)} is called.
 *
 * @author Gisa Schaefer
 * @since 7.3.1
 */
final class DoubleAutoColumn implements Column {

	private static final long serialVersionUID = 1L;

	/**
	 * Building block of a {@link DoubleAutoColumn}.
	 *
	 * @author Gisa Schaefer
	 *
	 */
	static abstract class DoubleAutoChunk implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * the position of this chunk in {@link DoubleAutoColumn#chunks}
		 */
		final int id;

		/**
		 * the chunk array {@link DoubleAutoColumn#chunks}
		 */
		final DoubleAutoChunk[] chunks;

		DoubleAutoChunk(int id, DoubleAutoChunk[] chunks) {
			this.id = id;
			this.chunks = chunks;
		}

		/**
		 * Sets the value at the specified row to the given value. This assumes that row is the
		 * largest index set so far and no value for this row was set before. The necessary space
		 * must be allocated by {@link #ensure(int)} beforehand.
		 *
		 * @param row
		 *            the row that should be set
		 * @param value
		 *            the value that should be set at the row
		 */
		abstract void setLast(int row, double value);

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

		/**
		 * Signals that no further calls to {@link #ensure(int)} and {@link #setLast(int,double)}
		 * will be made.
		 */
		void complete() {}

	}

	private DoubleAutoChunk[] chunks = new DoubleAutoChunk[Integer.MAX_VALUE / AutoColumnUtils.CHUNK_SIZE + 1];
	private int position = 0;
	private int chunkCount = 0;
	private int ensuredSize = 0;

	/**
	 * Constructs a column with enough chunks to fit size values.
	 *
	 * @param size
	 *            the size of the column
	 */
	DoubleAutoColumn(int size) {
		ensure(size);
	}

	/**
	 * {@inheritDoc}
	 *
	 * Must not be called before {@link #complete()} was called.
	 */
	@Override
	public double get(int row) {
		return chunks[row >> AutoColumnUtils.CHUNK_SIZE_EXP].get(row & AutoColumnUtils.CHUNK_MODULO_MASK);
	}

	/**
	 * {@inheritDoc}
	 *
	 * Must not be called before {@link #complete()} was called.
	 */
	@Override
	public void set(int row, double value) {
		chunks[row >> AutoColumnUtils.CHUNK_SIZE_EXP].set(row & AutoColumnUtils.CHUNK_MODULO_MASK, value);
	}

	@Override
	public void append(double value) {
		chunks[position >> AutoColumnUtils.CHUNK_SIZE_EXP].setLast(position & AutoColumnUtils.CHUNK_MODULO_MASK, value);
		position++;
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
//				chunks[chunkCount] = new DoubleAutoDenseChunk(chunks, chunkCount, chunkSize);
				chunkCount++;
			}
			rowsLeft -= chunkSize;
		}

		ensuredSize = size;
	}

	@Override
	public void complete() {
		for (int i = 0; i < chunkCount; i++) {
			chunks[i].complete();
		}
	}
}
