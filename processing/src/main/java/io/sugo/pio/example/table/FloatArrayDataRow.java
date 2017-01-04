package io.sugo.pio.example.table;

/**
 * Implementation of DataRow that is backed by a float array. Please note that for most applications
 * the precision of floats should be high enough. The highest precision is provided by
 * {@link DoubleArrayDataRow}s but these need the double amount
 * compared to these float representations which are therefore a good trade-off between precision
 * and memory usage.
 * 
 * @author Ingo Mierswa
 */
public class FloatArrayDataRow extends DataRow {

	private static final long serialVersionUID = -3691818538613297744L;

	/** Holds the data for all attributes. */
	private float[] data;

	/** Creates a new data row backed by an primitive array. */
	public FloatArrayDataRow(float[] data) {
		this.data = data;
	}

	/** Returns the desired data for the given index. */
	@Override
	protected double get(int index, double defaultValue) {
		return data[index];
	}

	/** Sets the given data for the given index. */
	@Override
	protected synchronized void set(int index, double value, double defaultValue) {
		data[index] = (float) value;
	}

	/**
	 * Creates a new array of the given size if necessary and copies the data into the new array.
	 */
	@Override
	protected synchronized void ensureNumberOfColumns(int numberOfColumns) {
		if (data.length >= numberOfColumns) {
			return;
		}
		float[] newData = new float[numberOfColumns];
		System.arraycopy(data, 0, newData, 0, data.length);
		data = newData;
	}

	/** Returns a string representation of the data row. */
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			result.append((i == 0 ? "" : ",") + data[i]);
		}
		return result.toString();
	}

	@Override
	public int getType() {
		return DataRowFactory.TYPE_FLOAT_ARRAY;
	}
}
