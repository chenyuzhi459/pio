package io.sugo.pio.datatable;

/**
 * A data list that contains Object arrays that record process results or other data. Each row can
 * consist of an id and an object array which represents the data.
 * 
 * @author Ingo Mierswa
 */
public interface DataTableRow {

	/** Returns the Id of this table row. */
	public String getId();

	/** Returns the i-th value. */
	public double getValue(int index);

	/** Returns the total number of values. */
	public int getNumberOfValues();
}
