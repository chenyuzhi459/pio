package io.sugo.pio.example.table.column;

import java.io.Serializable;


/**
 * This interface is the basis for the columns used in the {@link ColumnarExampleTable}.
 * Implementing classes provide their own internal data structure.
 *
 * @author Jan Czogalla
 * @see ColumnarExampleTable
 * @since 7.3
 *
 */
interface Column extends Serializable {

	/**
	 * Gets the value at the specified row.
	 *
	 * @param row
	 *            the row that should be looked up
	 * @return the value at the specified row
	 */
	double get(int row);

	/**
	 * Appends the given value to the column. Note that this operation is unchecked, make sure to
	 * {@link #ensure(int)} a sufficient size before.
	 *
	 * @param value
	 *            the value to append
	 */
	void append(double value);

	/**
	 * Sets the value at the specified row to the given value.
	 *
	 * @param row
	 *            the row that should be set
	 * @param value
	 *            the value that should be set at the row
	 */
	void set(int row, double value);

	/**
	 * Ensures that the internal data structure can hold up to {@code size} values.
	 *
	 * @param size
	 *            the size that should be ensured
	 */
	void ensure(int size);

	/**
	 * Completes the column (optional). Invoking this method signals that no further calls to
	 * {@link #ensure(int)} and {@link #append(double)} will be made.
	 */
	default void complete() {};

}
