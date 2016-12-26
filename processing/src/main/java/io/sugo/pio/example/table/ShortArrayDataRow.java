package io.sugo.pio.example.table;

/**
 * Implementation of DataRow that is backed by a short array. Please note that using this data row
 * is quite efficient but only non-floating numbers between -32,768 to 32,767 or the same number of
 * nominal values for each column.
 * 
 * @author Ingo Mierswa
 */
public class ShortArrayDataRow extends DataRow {

	private static final long serialVersionUID = -1839048476500092847L;

	/** Holds the data for all attributes. */
	private short[] data;

	/** Creates a new data row backed by an primitive array. */
	public ShortArrayDataRow(short[] data) {
		this.data = data;
	}

	/** Returns the desired data for the given index. */
	@Override
	protected double get(int index, double defaultValue) {
		return data[index];
	}

	/** Sets the given data for the given attribute. */
	@Override
	protected synchronized void set(int index, double value, double defaultValue) {
		data[index] = (short) value;
	}

	/**
	 * Creates a new array of the given size if necessary and copies the data into the new array.
	 */
	@Override
	protected synchronized void ensureNumberOfColumns(int numberOfColumns) {
		if (data.length >= numberOfColumns) {
			return;
		}
		short[] newData = new short[numberOfColumns];
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
		return DataRowFactory.TYPE_SHORT_ARRAY;
	}
}
