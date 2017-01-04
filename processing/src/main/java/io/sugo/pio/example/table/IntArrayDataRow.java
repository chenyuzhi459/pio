package io.sugo.pio.example.table;

/**
 * Implementation of DataRow that is backed by an integer array.
 * 
 * @author Ingo Mierswa Exp $
 */
public class IntArrayDataRow extends DataRow {

	private static final long serialVersionUID = -8089560930865510003L;

	/** Holds the data for all attributes. */
	private int[] data;

	/** Creates a new data row backed by an primitive array. */
	public IntArrayDataRow(int[] data) {
		this.data = data;
	}

	@Override
	protected double get(int index, double defaultValue) {
		return data[index];
	}

	/** Sets the given data for the given index. */
	@Override
	protected synchronized void set(int index, double value, double defaultValue) {
		data[index] = (int) value;
	}

	/**
	 * Creates a new array of the given size if necessary and copies the data into the new array.
	 */
	@Override
	protected synchronized void ensureNumberOfColumns(int numberOfColumns) {
		if (data.length >= numberOfColumns) {
			return;
		}
		int[] newData = new int[numberOfColumns];
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
		return DataRowFactory.TYPE_INT_ARRAY;
	}
}
