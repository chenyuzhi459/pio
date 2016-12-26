package io.sugo.pio.example.table.column;

/**
 * This implementation of {@link Column} is used to represent {@code null} attributes, i.e.
 * attributes that were removed and are not set in a {@link ColumnarExampleTable}. This makes
 * {@code null} checks unnecessary when iterating over all attribute indices.
 *
 * @author Jan Czogalla
 * @see Column
 * @see ColumnarExampleTable
 * @since 7.3
 *
 */
class NaNColumn implements Column {

	private static final long serialVersionUID = 1L;

	@Override
	public double get(int row) {
		return Double.NaN;
	}

	@Override
	public void set(int row, double value) {
		// do nothing
	}

	@Override
	public void ensure(int size) {
		// do nothing
	}

	@Override
	public void append(double value) {
		// do nothing
	}

}
