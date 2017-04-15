package io.sugo.pio.operator.io;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.*;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeBoolean;
import io.sugo.pio.parameter.ParameterTypeString;
import io.sugo.pio.parameter.PortProvider;
import io.sugo.pio.parameter.conditions.PortConnectedCondition;
import io.sugo.pio.ports.Port;
import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.io.Encoding;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.DateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * <p>
 * This operator can be used to write data into CSV files (Comma Separated Values). The values and
 * columns are separated by &quot;;&quot;. Missing data values are indicated by empty cells.
 * </p>
 *
 * @author Ingo Mierswa
 */
public class CSVExampleSetWriter extends AbstractStreamWriter {

	/** The parameter name for &quot;The CSV file which should be written.&quot; */
	public static final String PARAMETER_CSV_FILE = "csv_file";

	/** The parameter name for the column separator parameter. */
	public static final String PARAMETER_COLUMN_SEPARATOR = "column_separator";

	/** Indicates if the attribute names should be written as first row. */
	public static final String PARAMETER_WRITE_ATTRIBUTE_NAMES = "write_attribute_names";

	/**
	 * Indicates if nominal values should be quoted with double quotes. Quotes inside of nominal
	 * values will be escaped by a backslash.
	 */
	public static final String PARAMETER_QUOTE_NOMINAL_VALUES = "quote_nominal_values";

	public static final String PARAMETER_APPEND_FILE = "append_to_file";

	/**
	 * Indicates if date attributes are written as a formated string or as milliseconds past since
	 * January 1, 1970, 00:00:00 GMT
	 */
	// TODO introduce parameter which allows to determine the written format see
	// Nominal2Date operator
	public static final String PARAMETER_FORMAT_DATE = "format_date_attributes";

	public CSVExampleSetWriter() {
		super();
	}

	@Override
	public String getDefaultFullName() {
		return I18N.getMessage("pio.CSVExampleSetWriter.name");
	}

	@Override
	public OperatorGroup getGroup() {
		return OperatorGroup.dataSource;
	}

	@Override
	public String getDescription() {
		return I18N.getMessage("pio.CSVExampleSetWriter.description");
	}

	/**
	 * Writes the exampleSet with the {@link PrintWriter} out, using colSeparator as column
	 * separator.
	 *
	 * @param exampleSet
	 *            the example set to write
	 * @param out
	 *            the {@link PrintWriter}
	 * @param colSeparator
	 *            the column separator
	 * @param quoteNomValues
	 *            if {@code true} nominal values are quoted
	 * @param writeAttribNames
	 *            if {@code true} the attribute names are written into the first row
	 * @param formatDate
	 *            if {@code true} dates are formatted to "M/d/yy h:mm a", otherwise milliseconds
	 *            since the epoch are used
	 *
	 * @deprecated please use
	 *             {@link CSVExampleSetWriter#writeCSV(ExampleSet, PrintWriter, String, boolean, boolean, boolean, OperatorProgress)}
	 *             instead to support operator progress.
	 */
	@Deprecated
	public static void writeCSV(ExampleSet exampleSet, PrintWriter out, String colSeparator, boolean quoteNomValues,
								boolean writeAttribNames, boolean formatDate) {
		try {
			writeCSV(exampleSet, out, colSeparator, quoteNomValues, writeAttribNames, formatDate, null, null);
		} catch (ProcessStoppedException e) {
			// can not happen because we provide no OperatorProgressListener
		}
	}

	/**
	 * Writes the exampleSet with the {@link PrintWriter} out, using colSeparator as column
	 * separator.
	 *
	 * @param exampleSet
	 *            the example set to write
	 * @param out
	 *            the {@link PrintWriter}
	 * @param colSeparator
	 *            the column separator
	 * @param quoteNomValues
	 *            if {@code true} nominal values are quoted
	 * @param writeAttribNames
	 *            if {@code true} the attribute names are written into the first row
	 * @param formatDate
	 *            if {@code true} dates are formatted to "M/d/yy h:mm a", otherwise milliseconds
	 *            since the epoch are used
	 * @param operatorProgress
	 *            the {@link OperatorProgress} is used to provide a more detailed progress. Within
	 *            this method the progress will be increased by number of examples times the number
	 *            of attributes. If you do not want the operator progress, just provide <code> null
	 *            <code>.
	 */
	public static void writeCSV(ExampleSet exampleSet, PrintWriter out, String colSeparator, boolean quoteNomValues,
			boolean writeAttribNames, boolean formatDate, OperatorProgress operatorProgress) throws ProcessStoppedException {
		writeCSV(exampleSet, out, colSeparator, quoteNomValues, writeAttribNames, formatDate, null, operatorProgress);
	}

	/**
	 * Writes the exampleSet with the {@link PrintWriter} out, using colSeparator as column
	 * separator and infinitySybol to denote infinite values.
	 *
	 * @param exampleSet
	 *            the example set to write
	 * @param out
	 *            the {@link PrintWriter}
	 * @param colSeparator
	 *            the column separator
	 * @param quoteNomValues
	 *            if {@code true} nominal values are quoted
	 * @param writeAttribNames
	 *            if {@code true} the attribute names are written into the first row
	 * @param formatDate
	 *            if {@code true} dates are formatted to "M/d/yy h:mm a", otherwise milliseconds
	 *            since the epoch are used
	 * @param infinitySymbol
	 *            the symbol to use for infinite values; if {@code null} the default symbol
	 *            "Infinity" is used
	 *
	 * @deprecated please use
	 *             {@link CSVExampleSetWriter#writeCSV(ExampleSet, PrintWriter, String, boolean, boolean, boolean, String, OperatorProgress)}
	 *             to support operator progress.
	 */
	@Deprecated
	public static void writeCSV(ExampleSet exampleSet, PrintWriter out, String colSeparator, boolean quoteNomValues,
			boolean writeAttribNames, boolean formatDate, String infinitySymbol) {
		try {
			writeCSV(exampleSet, out, colSeparator, quoteNomValues, writeAttribNames, formatDate, infinitySymbol, null);
		} catch (ProcessStoppedException e) {
			// can not happen because we provide no OperatorProcessListener
		}
	}

	/**
	 * Writes the exampleSet with the {@link PrintWriter} out, using colSeparator as column
	 * separator and infinitySybol to denote infinite values.
	 *
	 * @param exampleSet
	 *            the example set to write
	 * @param out
	 *            the {@link PrintWriter}
	 * @param colSeparator
	 *            the column separator
	 * @param quoteNomValues
	 *            if {@code true} nominal values are quoted
	 * @param writeAttribNames
	 *            if {@code true} the attribute names are written into the first row
	 * @param formatDate
	 *            if {@code true} dates are formatted to "M/d/yy h:mm a", otherwise milliseconds
	 *            since the epoch are used
	 * @param infinitySymbol
	 *            the symbol to use for infinite values; if {@code null} the default symbol
	 *            "Infinity" is used
	 * @param opProg
	 *            the {@link OperatorProgress} is used to provide a more detailed progress. Within
	 *            this method the progress will be increased by number of examples times the number
	 *            of attributes. If you do not want the operator progress, just provide <code> null
	 *            <code>.
	 */
	public static void writeCSV(ExampleSet exampleSet, PrintWriter out, String colSeparator, boolean quoteNomValues,
			boolean writeAttribNames, boolean formatDate, String infinitySymbol, OperatorProgress opProg)
			throws ProcessStoppedException {
		String negativeInfinitySymbol = null;
		if (infinitySymbol != null) {
			negativeInfinitySymbol = "-" + infinitySymbol;
		}
		String columnSeparator = colSeparator;
		boolean quoteNominalValues = quoteNomValues;

		// write column names
		if (writeAttribNames) {
			Iterator<Attribute> a = exampleSet.getAttributes().allAttributes();
			boolean first = true;
			while (a.hasNext()) {
				if (!first) {
					out.print(columnSeparator);
				}
				Attribute attribute = a.next();
				String name = attribute.getName();
				if (quoteNominalValues) {
					name = name.replaceAll("\"", "'");
					name = "\"" + name + "\"";
				}
				out.print(name);
				first = false;
			}
			out.println();
		}

		// write data
		int progressCounter = 0;
		for (Example example : exampleSet) {
			Iterator<Attribute> a = exampleSet.getAttributes().allAttributes();
			boolean first = true;
			while (a.hasNext()) {

				Attribute attribute = a.next();
				if (!first) {
					out.print(columnSeparator);
				}
				if (!Double.isNaN(example.getValue(attribute))) {
					if (attribute.isNominal()) {
						String stringValue = example.getValueAsString(attribute);
						if (quoteNominalValues) {
							stringValue = stringValue.replaceAll("\"", "'");
							stringValue = "\"" + stringValue + "\"";
						}
						out.print(stringValue);
					} else {
						Double value = example.getValue(attribute);
						if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(attribute.getValueType(), Ontology.DATE_TIME)) {
							if (formatDate) {
								Date date = new Date(value.longValue());
								String s = DateFormat.getInstance().format(date);
								out.print(s);
							} else {
								out.print(value);
							}
						} else {
							if (value.isInfinite() && infinitySymbol != null) {
								if (Double.POSITIVE_INFINITY == value) {
									out.print(infinitySymbol);
								} else {
									out.print(negativeInfinitySymbol);
								}
							} else {
								out.print(value);
							}
						}

					}
				}
				first = false;
			}

			out.println();

			// trigger operator progress every 100 examples
			if (opProg != null) {
				++progressCounter;
				if (progressCounter % 100 == 0) {
					opProg.step(100);
					progressCounter = 0;
				}
			}
		}
	}

	@Override
	public void writeStream(ExampleSet exampleSet, java.io.OutputStream outputStream) throws OperatorException {

		String columnSeparator = getParameterAsString(PARAMETER_COLUMN_SEPARATOR);
		boolean quoteNominalValues = getParameterAsBoolean(PARAMETER_QUOTE_NOMINAL_VALUES);
		boolean writeAttribNames = getParameterAsBoolean(PARAMETER_WRITE_ATTRIBUTE_NAMES);
		boolean formatDate = getParameterAsBoolean(PARAMETER_FORMAT_DATE);
		try (PrintWriter out = new PrintWriter(new OutputStreamWriter(outputStream, Encoding.getEncoding(this)))) {
			// init operator progress
			getProgress().setTotal(exampleSet.size());
			writeCSV(exampleSet, out, columnSeparator, quoteNominalValues, writeAttribNames, formatDate, getProgress());
			getProgress().complete();
		}
	}

	@Override
	protected boolean supportsEncoding() {
		return true;
	}

	@Override
	protected boolean shouldAppend() {
		return getParameterAsBoolean(PARAMETER_APPEND_FILE);
	}

	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = new LinkedList<ParameterType>();
		types.add(makeFileParameterType());
		// types.add(new ParameterTypeFile(PARAMETER_CSV_FILE,
		// "The CSV file which should be written.", "csv", false));
		types.add(new ParameterTypeString(PARAMETER_COLUMN_SEPARATOR, I18N.getMessage("pio.CSVExampleSetWriter.column_separators"), ";", false));
		types.add(new ParameterTypeBoolean(PARAMETER_WRITE_ATTRIBUTE_NAMES,
				I18N.getMessage("pio.CSVExampleSetWriter.write_attribute_names"), true, false));
		types.add(new ParameterTypeBoolean(PARAMETER_QUOTE_NOMINAL_VALUES,
				I18N.getMessage("pio.CSVExampleSetWriter.quote_nominal_values"), true, false));
		types.add(new ParameterTypeBoolean(PARAMETER_FORMAT_DATE,
				I18N.getMessage("pio.CSVExampleSetWriter.format_date"),
				true, true));
		ParameterType type = new ParameterTypeBoolean(PARAMETER_APPEND_FILE,
				I18N.getMessage("pio.CSVExampleSetWriter.append_file"),
				false, false);
		type.registerDependencyCondition(new PortConnectedCondition(this, new PortProvider() {

			@Override
			public Port getPort() {
				return fileOutputPort;
			}
		}, true, false));
		types.add(type);
		types.addAll(super.getParameterTypes());
		return types;
	}

	@Override
	protected String getFileParameterName() {
		return PARAMETER_CSV_FILE;
	}

	@Override
	protected String[] getFileExtensions() {
		return new String[] { "csv" };
	}
}
