package io.sugo.pio.operator.io;


import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.table.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntToDoubleFunction;

/**
 * A {@link ExampleSetBuilder} based on a {@link MemoryExampleTable}.
 *
 * @author Gisa Schaefer
 * @since 7.3
 */
class MemoryExampleSetBuilder extends ExampleSetBuilder {

	/** the table that will be created and filled */
	private MemoryExampleTable table;

	/** number of empty rows to be added */
	private int blankSize;

	/** expected number of rows for the table */
	private int expectedSize;

	/** the reader to use for filling the table */
	private DataRowReader reader;

	/** the functions to use for filling the columns */
	private Map<Attribute, IntToDoubleFunction> columnFillers = new HashMap<>();

	/**
	 * Creates a builder that stores values in a {@link MemoryExampleTable} based on the given
	 * attributes.
	 *
	 * @param attributes
	 *            the {@link Attribute}s that the {@link ExampleSet} should contain
	 */
	MemoryExampleSetBuilder(List<Attribute> attributes) {
		super(attributes);
		setTableIndices();
	}

	/**
	 * Creates a builder that stores values in a {@link MemoryExampleTable} based on the given
	 * attributes.
	 *
	 * @param attributes
	 *            the {@link Attribute}s that the {@link ExampleSet} should contain
	 */
	MemoryExampleSetBuilder(Attribute... attributes) {
		super(attributes);
		setTableIndices();
	}

	@Override
	public ExampleSetBuilder withBlankSize(int numberOfRows) {
		this.blankSize = numberOfRows;
		return this;
	}

	@Override
	public ExampleSetBuilder withExpectedSize(int numberOfRows) {
		this.expectedSize = numberOfRows;
		return this;
	}

	@Override
	public ExampleSetBuilder withDataRowReader(DataRowReader reader) {
		this.reader = reader;
		return this;
	}

	@Override
	public ExampleSetBuilder addDataRow(DataRow dataRow) {
		if (table == null) {
			table = createTable();
		}
		table.addDataRow(dataRow);
		return this;
	}

	@Override
	public ExampleSetBuilder addRow(double[] row) {
		if (table == null) {
			table = createTable();
		}
		table.addDataRow(new DoubleArrayDataRow(row));
		return this;
	}

	@Override
	public ExampleSetBuilder withColumnFiller(Attribute attribute, IntToDoubleFunction valueForRow) {
		columnFillers.put(attribute, valueForRow);
		return this;
	}

	@Override
	protected ExampleTable getExampleTable() {
		if (table == null) {
			table = createTable();
		}
		if (reader != null) {
			while (reader.hasNext()) {
				table.addDataRow(reader.next());
			}
		}
		if (blankSize > 0) {
			addBlankRows();
		}
		if (columnFillers.size() > 0) {
			writeColumnValues();
		}

		return table;
	}

	/**
	 * Sets the table indices so that {@code dataRow.set(attribute,value)} can be used for the
	 * attributes. Calling {@code new MemoryExampleTable(getAttributes)} has the same effect.
	 */
	private void setTableIndices() {
		int i = 0;
		for (Attribute attribute : getAttributes()) {
			attribute.setTableIndex(i++);
		}
	}

	/**
	 * Writes the values provided by the {@link #columnFillers} into the table.
	 */
	private void writeColumnValues() {
		DataRowReader tableReader = table.getDataRowReader();
		int k = 0;
		while (tableReader.hasNext()) {
			DataRow dataRow = tableReader.next();
			for (Map.Entry<Attribute, IntToDoubleFunction> entry : columnFillers.entrySet()) {
				dataRow.set(entry.getKey(), entry.getValue().applyAsDouble(k));
			}
			k++;
		}
	}

	/**
	 * @return the table to store the data in
	 */
	@SuppressWarnings("deprecation")
	private MemoryExampleTable createTable() {
		if (expectedSize > 0 || blankSize > 0) {
			return new MemoryExampleTable(getAttributes(), Math.max(expectedSize, blankSize));
		} else {
			return new MemoryExampleTable(getAttributes());
		}
	}

	/**
	 * Adds {@link #blankSize} blank rows of type {@link DataRowFactory#TYPE_DOUBLE_ARRAY}.
	 */
	private void addBlankRows() {
		DataRowFactory rowFactory = new DataRowFactory(DataRowFactory.TYPE_DOUBLE_ARRAY,
				DataRowFactory.POINT_AS_DECIMAL_CHARACTER);
		int size = getAttributes().size();
		for (int i = 0; i < blankSize; i++) {
			table.addDataRow(rowFactory.create(size));
		}
	}
}
