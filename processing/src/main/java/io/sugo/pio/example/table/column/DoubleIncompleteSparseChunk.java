package io.sugo.pio.example.table.column;

/**
 * Sparse {@link DoubleIncompleteAutoChunk} for double value data of a
 * {@link DoubleIncompleteAutoColumn}.
 *
 * @author Gisa Schaefer
 * @since 7.3.1
 */
final class DoubleIncompleteSparseChunk extends DoubleIncompleteAutoChunk {

	private static final long serialVersionUID = 1L;

	private final DoubleSparseChunk sparse;
	private int ensuredSize;
	private int maxSetRow;

	DoubleIncompleteSparseChunk(int id, DoubleIncompleteAutoChunk[] chunks, double defaultValue) {
		super(id, chunks);
		sparse = new DoubleSparseChunk(defaultValue);
	}

	@Override
	double get(int row) {
		return sparse.get(row);
	}

	@Override
	void set(int row, double value) {
		maxSetRow = Math.max(row, maxSetRow);
		if (sparse.set(row, value)) {
			changeToDense(maxSetRow);
		}
	}

	@Override
	void ensure(int size) {
		ensuredSize = size;
		sparse.ensure(size);
	}

	private void changeToDense(int max) {
		DoubleIncompleteAutoChunk dense = new DoubleIncompleteDenseChunk(id, chunks, ensuredSize, true);
		for (int i = 0; i <= max; i++) {
			dense.set(i, get(i));
		}
		chunks[id] = dense;
	}

}
