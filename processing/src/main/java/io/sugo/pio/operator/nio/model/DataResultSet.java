package io.sugo.pio.operator.nio.model;


import io.sugo.pio.tools.Ontology;

import java.util.Date;


/**
 * This interface represents a ResultSet like view on a data source. It is just the interface for
 * iterating over the raw data. The
 *
 * @author Tobias Malbrecht, Sebastian Loh, Sebastian Land
 */
public interface DataResultSet extends AutoCloseable {

	public enum ValueType {
		STRING(Ontology.POLYNOMINAL), DATE(Ontology.DATE_TIME),
		// INTEGER(Ontology.INTEGER),
		NUMBER(Ontology.NUMERICAL), EMPTY(Ontology.ATTRIBUTE_VALUE);

		private final int rapidMinerAttributeType;

		private ValueType(int rapidMinerAttributeType) {
			this.rapidMinerAttributeType = rapidMinerAttributeType;
		}

		public int getRapidMinerAttributeType() {
			return rapidMinerAttributeType;
		}
	}

	/**
	 * This returns if another row exists.
	 */
	boolean hasNext();

	/**
	 * Proceed to the next row if existent. Will throw NoSuchElementException if no further row
	 * exists
	 *
	 *
	 * @return
	 */
	void next();

	/**
	 * Returns the number of columns found so far. If there are any rows containing more columns
	 * that this value, an error has to be registered.
	 */
	int getNumberOfColumns();

	/**
	 * This returns the names of the columns according to the underlying technical system. For
	 * example the database column name might be returned. This method is only used, if there's no
	 * user specific setting in the {@link DataResultSetTranslationConfiguration} or annotations
	 * present in the {@link DataResultSetTranslationConfiguration}.
	 */
	String[] getColumnNames();

	/**
	 * Returns whether the value in the specified column in the current row is missing.
	 *
	 * @param columnIndex
	 *            index of the column
	 * @return
	 */
	boolean isMissing(int columnIndex);

	/**
	 * Returns a numerical value contained in the specified column in the current row. Should return
	 * null if the value is not a numerical or if the value is missing.
	 *
	 * @param columnIndex
	 * @return
	 */
	Number getNumber(int columnIndex) throws ParseException;

	/**
	 * Returns a nominal value contained in the specified column in the current row. Should return
	 * null if the value is not a nominal or a kind of string type or if the value is missing.
	 *
	 * @param columnIndex
	 * @return
	 */
	String getString(int columnIndex) throws ParseException;

	/**
	 * Returns a date, time or date_time value contained in the specified column in the current row.
	 * Should return null if the value is not a date or time value or if the value is missing.
	 *
	 * @param columnIndex
	 * @return
	 */
	Date getDate(int columnIndex) throws ParseException;

	/**
	 *
	 * @return The type which most closely matches the value type of the underlying data source. The
	 *         corresponding getter method for this type must not throw an RuntimeException when
	 *         invoked for this column.
	 */
	ValueType getNativeValueType(int columnIndex) throws ParseException;

	/**
	 * Closes the data source. May tear down a database connection or close a file which is read
	 * from. The underlying {@link DataResultSetFactory} may choose to keep the connection open for
	 * performance reasons and must be closed independently.
	 *
	 */
	@Override
	void close();

	/**
	 * This will return an integer array containing the value types of all columns. This might be
	 * determined by the underlying technical system like data bases. If not, just an Array
	 * Containing 0 might be returned.
	 */
	int[] getValueTypes();

	/**
	 * @return the current {@code 0} based row index of the current parsed row. In case no row has
	 *         been parsed yet, {@code -1} will be returned.
	 */
	int getCurrentRow();

}
