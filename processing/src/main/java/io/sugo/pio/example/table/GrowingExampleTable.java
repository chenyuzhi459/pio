package io.sugo.pio.example.table;

/**
 * {@link ExampleTable} to which rows can be added.
 *
 * @author Gisa Schaefer
 * @since 7.3
 */
public interface GrowingExampleTable extends ExampleTable {

	/**
	 * Adds the given row at the end of the table
	 * 
	 * @param row
	 *            the row to be added
	 */
	void addDataRow(DataRow row);
}
