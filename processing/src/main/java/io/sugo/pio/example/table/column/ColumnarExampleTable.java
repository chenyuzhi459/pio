package io.sugo.pio.example.table.column;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.table.*;
import io.sugo.pio.example.util.ExampleSets;
import io.sugo.pio.tools.Ontology;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 * {@linkplain Column} oriented, dense example table. <br/>
 * Caution: This class is not part of the official API. Please do not use it, instead use methods
 * provided in {@link ExampleSets}.
 *
 * @see Column
 * @since 7.3
 */
public class ColumnarExampleTable extends AbstractExampleTable implements GrowingExampleTable {

	private static final long serialVersionUID = 1L;

	/** Non-empty tables will allocate at least memory for {@value} rows. */
	private static final int MIN_NON_EMPTY_SIZE = 8;

	/**
	 * Empty column as filler for non-existent attributes. Prevents {@link NullPointerException}s
	 * and makes {@code null} checks unnecessary
	 */
	private static final Column NAN_COLUMN = new NaNColumn();

	/**
	 * View of a single data row. The view itself does not store any data.
	 *
	 * @author Michael Knopf
	 */
	private class RowView extends DataRow {

		private static final long serialVersionUID = 1L;

		private final int row;

		public RowView(int index) {
			row = index;
		}

		@Override
		protected double get(int column, double defaultValue) {
			return columns[column].get(row);
		}

		@Override
		protected void set(int column, double value, double defaultValue) {
			columns[column].set(row, value);
		}

		@Override
		protected void ensureNumberOfColumns(int columns) {
			// not necessary
		}

		@Override
		public void trim() {
			// no data to trim
		}

		@Override
		public int getType() {
			return DataRowFactory.TYPE_COLUMN_VIEW;
		}

		@Override
		public String toString() {
			StringBuffer result = new StringBuffer();
			for (int i = 0; i < getNumberOfAttributes(); i++) {
				result.append((i == 0 ? "" : ",") + columns[i].get(row));
			}
			return result.toString();
		}

	}

	/**
	 * Reader for data row views.
	 *
	 * @author Michael Knopf
	 */
	private class RowReader implements DataRowReader {

		private int current = 0;
		private final int last = size;

		@Override
		public boolean hasNext() {
			return current < last;
		}

		@Override
		public DataRow next() {
			return new RowView(current++);
		}

	}

	private Column[] columns;

	private int size;
	private int sizeLimit;
	private boolean completable;

	/**
	 * Creates a new, empty data table with the given attributes.
	 *
	 * @param attributes
	 *            the table's attributes
	 */
	public ColumnarExampleTable(List<Attribute> attributes) {
		this(attributes, false);
	}

	/**
	 * Creates a new, empty data table with the given attributes.
	 *
	 * @param attributes
	 *            the table's attributes
	 * @param completable
	 *            whether {@link #complete()} will be called when the number of rows is final and
	 *            before the first reading of values
	 */
	public ColumnarExampleTable(List<Attribute> attributes, boolean completable) {
		super(attributes);
		int attributeCount = super.getNumberOfAttributes();

		columns = new Column[attributeCount];

		size = 0;
		sizeLimit = 0;

		// must be set before updating columns
		this.completable = completable;

		for (int i = 0; i < attributes.size(); i++) {
			updateColumn(i, attributes.get(i));
		}
	}

	/**
	 * Constructor for a shallow clone of the table. The data columns are not cloned.
	 *
	 * @param table
	 *            the table to clone
	 */
	private ColumnarExampleTable(ColumnarExampleTable table) {
		super(table);
		this.columns = Arrays.copyOf(table.columns, table.columns.length);
		this.size = table.size;
		this.sizeLimit = table.sizeLimit;
		this.completable = table.completable;
	}

	@Override
	public synchronized int addAttribute(Attribute attribute) {
		int newIndex = super.addAttribute(attribute);
		if (columns != null) {
			ensureWidth(super.getNumberOfAttributes());
			updateColumn(newIndex, attribute);
		}
		return newIndex;
	}

	@Override
	public synchronized void removeAttribute(int index) {
		super.removeAttribute(index);
		updateColumn(index, null);
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public DataRowReader getDataRowReader() {
		return new RowReader();
	}

	@Override
	public DataRow getDataRow(int index) {
		return new RowView(index);
	}

	/**
	 * Adds a copy of the given data row to the example table. Will throw an
	 * {@link ArrayIndexOutOfBoundsException} if the data row does not fit the attributes of this
	 * table.
	 *
	 * @param dataRow
	 *            the new data row
	 * @throws RuntimeException
	 *             May be thrown if the data row does not fit the attributes of the underlying
	 *             table, depending on the data row implementation.
	 */
	@Override
	public void addDataRow(DataRow dataRow) {
		ensureHeight(size + 1);
		int numberOfAttributes = super.getNumberOfAttributes();
		// prevent index out of bounds if row size does not match
		for (int i = 0; i < numberOfAttributes; i++) {
			Attribute attribute = getAttribute(i);
			columns[i].append(dataRow.get(attribute));
		}
		size++;
	}

	/**
	 * Adds a copy of the given row to the example table.
	 *
	 * @param row
	 *            the row as double array
	 */
	public void addRow(double[] row) {
		ensureHeight(size + 1);
		int min = Math.min(super.getNumberOfAttributes(), row.length);
		for (int i = 0; i < min; i++) {
			columns[i].append(row[i]);
		}
		size++;
	}

	/**
	 * Adds numberOfRows blank rows to the table. These rows can be filled afterwards by
	 * fillColumn or using {@link #getDataRowReader}.
	 *
	 * @param numberOfRows
	 *            the number of empty rows to add
	 */
	public void addBlankRows(int numberOfRows) {
		if (numberOfRows > 0) {
			int newSize = size + numberOfRows;
			if (newSize > sizeLimit) {
				ensureHeight(newSize);
			}
			size = newSize;
		}
	}

	/**
	 * Sets the expected number of rows. Use this if you know in advance how many rows will be added
	 * by {@link #addRow} or {@link #addDataRow}. Using this method prevents unnecessary resizing if
	 * the container for row values becomes to small.
	 *
	 * @param expectedNumberOfRows
	 *            the expected number of rows
	 */
	public void setExpectedSize(int expectedNumberOfRows) {
		if (expectedNumberOfRows <= sizeLimit) {
			return;
		}
		updateHeight(expectedNumberOfRows);
	}

	/**
	 * Signals that the number of rows is final. Must be called when using the constructor
	 * {@link #ColumnarExampleTable(List, boolean)} with completable {@code true} before the first
	 * time that values are read.
	 */
	public void complete() {
		completable = false;
		for (Column column : columns) {
			column.complete();
		}
	}

	/**
	 * Creates a shallow clone of the table and removes all columns not contained in attributes.
	 *
	 * @param attributes
	 *            the attributes to determine which columns to keep
	 * @return a new table with only column data when the column is associated to an attribute from
	 *         attributes
	 */
	public ColumnarExampleTable columnCleanupClone(Attributes attributes) {
		int attributeCount = this.getNumberOfAttributes();
		ColumnarExampleTable newTable = new ColumnarExampleTable(this);
		// check which table indices are still in use
		boolean[] usedIndices = new boolean[attributeCount];
		for (Iterator<Attribute> allIterator = attributes.allAttributes(); allIterator.hasNext();) {
//			Attribute attribute = allIterator.next();
//			usedIndices[attribute.getTableIndex()] = true;
		}

		// remove unused attributes and their columns
		for (int i = 0; i < attributeCount; i++) {
			if (!usedIndices[i]) {
				newTable.removeAttribute(i);
			}
		}
		return newTable;
	}

	/**
	 * Ensures that the data table can store up to the given number of rows. Invoking this method
	 * does not change the size of the table!
	 * <p>
	 * The implementation resizes the column sizes using the same strategy as the JRE's array list
	 * implementation (enlarges arrays by ~50%).
	 *
	 * @param height
	 *            number of rows
	 */
	private void ensureHeight(int height) {
		if (height <= sizeLimit) {
			return;
		}
		int newHeight = Math.max(Math.max(MIN_NON_EMPTY_SIZE, height), sizeLimit + (sizeLimit >> 1));
		updateHeight(newHeight);
	}

	/**
	 * Uses the {@link Column#ensure(int) Column.ensure} method of each column to ensure the
	 * newHeight.
	 *
	 * @param newHeight
	 *            the new height of the table
	 */
	private void updateHeight(int newHeight) {
		for (int i = 0; i < super.getNumberOfAttributes(); i++) {
			columns[i].ensure(newHeight);
		}
		sizeLimit = newHeight;
	}

	/**
	 * Ensures that the data table can store up to the given number of columns. Invoking this method
	 * does not create a new attribute.
	 *
	 * @param width
	 *            number of columns
	 */
	private void ensureWidth(int width) {
		if (width <= columns.length) {
			return;
		}
		int newWidth = Math.max(Math.max(width, MIN_NON_EMPTY_SIZE), columns.length + (columns.length >> 1));
		columns = Arrays.copyOf(columns, newWidth);
	}

	/**
	 * Updates the given column with respect to the type of the associated attribute. If an
	 * attribute is removed, a reference to {@link #NAN_COLUMN} is set to prevent
	 * {@link NullPointerException}s when iterating over all attribute indices.
	 *
	 * @param column
	 *            the column to update
	 * @param attribute
	 *            the associated attribute
	 */
	private void updateColumn(final int column, Attribute attribute) {
		if (attribute == null) {
			columns[column] = NAN_COLUMN;
			return;
		}
		switch (attribute.getValueType()) {
			case Ontology.BINOMINAL:
				columns[column] = new ByteArrayColumn(sizeLimit);
				break;
			case Ontology.NOMINAL:
			case Ontology.POLYNOMINAL:
				columns[column] = completable ? new IntegerAutoColumn(sizeLimit)
						: new IntegerIncompleteAutoColumn(sizeLimit);
				break;
			default:
				columns[column] = completable ? new DoubleAutoColumn(sizeLimit) : new DoubleIncompleteAutoColumn(sizeLimit);
				break;
		}
	}

}
