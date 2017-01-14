package io.sugo.pio.datatable;

import java.io.Serializable;


/**
 * A data list that contains Object arrays that record process results or other data. Each row can
 * consist of an id and an object array which represents the data.
 * 
 * @author Ingo Mierswa, Marius Helf
 */
public class SimpleDataTableRow implements DataTableRow, Serializable {

	private static final long serialVersionUID = 1L;

	private double[] row;

	private String id;

	/**
	 * Creates a SimpleDataTableRow with the same values as other.
	 */
	public SimpleDataTableRow(DataTableRow other) {
		copyValuesFromOtherRow(other);
	}

	public SimpleDataTableRow(double[] row) {
		this(row, null);
	}

	public SimpleDataTableRow(double[] row, String id) {
		this.row = row;
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public double getValue(int index) {
		return row[index];
	}

	@Override
	public int getNumberOfValues() {
		return row.length;
	}

	public void copyValuesFromOtherRow(DataTableRow other) {
		int numberOfValues = other.getNumberOfValues();
		row = new double[numberOfValues];
		for (int i = 0; i < numberOfValues; ++i) {
			row[i] = other.getValue(i);
		}
	}
}
