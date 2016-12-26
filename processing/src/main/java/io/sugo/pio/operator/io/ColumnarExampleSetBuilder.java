package io.sugo.pio.operator.io;


import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.table.DataRow;
import io.sugo.pio.example.table.DataRowReader;
import io.sugo.pio.example.table.ExampleTable;
import io.sugo.pio.example.table.column.ColumnarExampleTable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.IntToDoubleFunction;


/**
 * An {@link ExampleSetBuilder} based on a {@link ColumnarExampleTable}.
 *
 * @author Gisa Schaefer
 * @since 7.3
 */
class ColumnarExampleSetBuilder extends ExampleSetBuilder {

	/** the table that will be created and filled */
	private ColumnarExampleTable table;

	/** number of empty rows to be added */
	private int blankSize;

	/** the reader to use for filling the table */
	private DataRowReader reader;

	/** the functions to use for filling the columns */
	private Map<Attribute, IntToDoubleFunction> columnFillers = new HashMap<>();

	/** stores whether rows were added */
	private boolean rowsAdded = false;

	/**
	 * Creates a builder that stores values in a {@link ColumnarExampleTable} based on the given
	 * attributes.
	 *
	 * @param attributes
	 *            the {@link Attribute}s that the {@link ExampleSet} should contain
	 */
	ColumnarExampleSetBuilder(List<Attribute> attributes) {
		super(attributes);
		table = new ColumnarExampleTable(attributes, true);
	}

	/**
	 * Creates a builder that stores values in a {@link ColumnarExampleTable} based on the given
	 * attributes.
	 *
	 * @param attributes
	 *            the {@link Attribute}s that the {@link ExampleSet} should contain
	 */
	ColumnarExampleSetBuilder(Attribute... attributes) {
		super(attributes);
		table = new ColumnarExampleTable(Arrays.asList(attributes), true);
	}

	@Override
	public ExampleSetBuilder withBlankSize(int numberOfRows) {
		this.blankSize = numberOfRows;
		return this;
	}

	@Override
	public ExampleSetBuilder withExpectedSize(int numberOfRows) {
		table.setExpectedSize(numberOfRows);
		return this;
	}

	@Override
	public ExampleSetBuilder withDataRowReader(DataRowReader reader) {
		this.reader = reader;
		return this;
	}

	@Override
	public ExampleSetBuilder addDataRow(DataRow dataRow) {
		table.addDataRow(dataRow);
		rowsAdded = true;
		return this;
	}

	@Override
	public ExampleSetBuilder addRow(double[] row) {
		table.addRow(row);
		rowsAdded = true;
		return this;
	}

	@Override
	public ExampleSetBuilder withColumnFiller(Attribute attribute, IntToDoubleFunction columnFiller) {
		columnFillers.put(attribute, columnFiller);
		return this;
	}

	@Override
	protected ExampleTable getExampleTable() {
		if (reader != null) {
			rowsAdded = true;
			while (reader.hasNext()) {
				table.addDataRow(reader.next());
			}
		}
		if (blankSize > 0) {
			table.addBlankRows(blankSize);
		}
		if (columnFillers.size() > 0) {
			writeColumnValues();
		}

		table.complete();

		return table;
	}

	/**
	 * Writes the values provided by the {@link #columnFillers} into the table.
	 */
	private void writeColumnValues() {
		for (Entry<Attribute, IntToDoubleFunction> entry : columnFillers.entrySet()) {
			if (rowsAdded) {
				// must reset the column when rows were added so that the auto column mechanism can
				// work
//				table.resetColumn(entry.getKey());
			}
//			table.fillColumn(entry.getKey(), entry.getValue());
		}
	}

}
