package io.sugo.pio.example.table.column;

/**
 * A sparse chunk that stores double values.
 *
 * @author Jan Czogalla
 * @since 7.3.1
 */
class DoubleSparseChunk extends AbstractSparseChunk {

	private static final long serialVersionUID = 1L;

	private double[] data = AutoColumnUtils.EMPTY_DOUBLE_ARRAY;

	DoubleSparseChunk(double defaultValue) {
		super(defaultValue);
	}

	@Override
	void removeValueIndex(int index, int length) {
		double[] tmp = data;
		if (length != tmp.length) {
			tmp = new double[length];
		}
		copy(data, tmp, index, index + 1, index, valueCount);
		data = tmp;
	}

	@Override
	void insertValueIndex(int index, int length) {
		double[] tmp = data;
		if (length != tmp.length) {
			tmp = new double[length];
		}
		copy(data, tmp, index, index, index + 1, valueCount);
		data = tmp;
	}

	@Override
	double getValue(int index) {
		return data[index];
	}

	@Override
	void setValue(int index, double value) {
		data[index] = value;
	}

}
