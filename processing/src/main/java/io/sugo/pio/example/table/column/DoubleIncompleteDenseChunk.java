package io.sugo.pio.example.table.column;


import java.util.Arrays;


/**
 * Dense {@link DoubleIncompleteAutoChunk} for double value data of a
 * {@link DoubleIncompleteAutoColumn}.
 *
 * @author Gisa Schaefer
 * @since 7.3.1
 */
final class DoubleIncompleteDenseChunk extends DoubleIncompleteAutoChunk {

	private static final long serialVersionUID = 1L;

	private boolean undecided;
	private int ensuredSize;
	private int setCalls = 0;
	private int maxSetRow = 0;

	private double[] data = AutoColumnUtils.EMPTY_DOUBLE_ARRAY;

	DoubleIncompleteDenseChunk(int id, DoubleIncompleteAutoChunk[] chunks, int size) {
		this(id, chunks, size, false);
	}

	DoubleIncompleteDenseChunk(int id, DoubleIncompleteAutoChunk[] chunks, int size, boolean stayDense) {
		super(id, chunks);
		this.undecided = !stayDense;
		ensure(size);
	}

	@Override
	double get(int row) {
		return data[row];
	}

	@Override
	void set(int row, double value) {
		data[row] = value;
		setCalls++;
		maxSetRow = Math.max(maxSetRow, row);

		if (row == AutoColumnUtils.THRESHOLD_CHECK_FOR_SPARSE - 1 && undecided && isSetUsedToAppend()) {
			undecided = false;
			checkSparse();
		}
	}

	/**
	 * @return whether the calls {@link #set(int, double)} probably came in order
	 */
	private boolean isSetUsedToAppend() {
		return setCalls - 1 == maxSetRow;
	}

	@Override
	void ensure(int size) {
		ensuredSize = size;
		data = Arrays.copyOf(data, size);
	}

	/**
	 * Finds the most frequent value in the values set until now. If this value if frequent enough,
	 * it changes to a sparse representation.
	 */
	private void checkSparse() {
		AutoColumnUtils.DensityResult result = AutoColumnUtils.checkDensity(data);

		if (result.density < AutoColumnUtils.THRESHOLD_SPARSE_DENSITY) {
			double defaultValue = result.mostFrequentValue;
			DoubleIncompleteAutoChunk sparse = new DoubleIncompleteSparseChunk(id, chunks, defaultValue);
			sparse.ensure(ensuredSize);
			boolean isNaN = Double.isNaN(defaultValue);
			for (int i = 0; i < AutoColumnUtils.THRESHOLD_CHECK_FOR_SPARSE; i++) {
				double value = data[i];
				// only set non-default values
				if (isNaN ? !Double.isNaN(value) : value != defaultValue) {
					sparse.set(i, value);
				}
			}
			chunks[id] = sparse;
		}
	}

}
