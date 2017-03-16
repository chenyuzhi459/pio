package io.sugo.pio.operator.io;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.table.AttributeFactory;
import io.sugo.pio.example.table.AttributeTypeException;
import io.sugo.pio.operator.Annotations;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.parameter.*;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.tools.Ontology;

import java.io.IOException;
import java.util.*;


/**
 * Abstract super class of all example sources reading from files.
 *
 * @author Tobias Malbrecht
 * @author Sebastian Loh (29.04.2010)
 */
public abstract class AbstractDataReader extends AbstractReader<ExampleSet> {

	public static final int PREVIEW_LINES = 300;

	/**
	 * DO NOT SET THIS PARAMETER DIRECTLY. USE THE
	 * {@link AbstractDataReader#setErrorTolerant(boolean)} in order to cache the value.
	 *
	 * Indicates whether the reader tolerates values, which do not match a attributes value type.
	 * <p>
	 * For example if the value type is NUMERICAL and the reader reads a string. If
	 * {@link AbstractDataReader#PARAMETER_ERROR_TOLERANT} is <code>true</code>, the reader writes a
	 * missing value, if it is <code>false</code> the reader throws an exception. The reader
	 * replaces also a binomial value type by nominal the attributes domain has more then two values
	 * and this parameter is checked.
	 */
	public static final String PARAMETER_ERROR_TOLERANT = "read_not_matching_values_as_missings";

	/**
	 * Hidden parameter in order to remember whether attributes names, which are defined by the user
	 * are used or not. This parameter is set <code>true</code> when the user uses the import
	 * wizards in order to configure the reader.
	 */
	private static final String PARAMETER_ATTRIBUTE_NAMES_DEFINED_BY_USER = "attribute_names_already_defined";

	/**
	 * This parameter holds the hole information about the attribute columns. I.e. which attributes
	 * are defined, the names, what value type they have, whether the att. is selected,
	 */
	private static final String PARAMETER_META_DATA = "data_set_meta_data_information";

	/**
	 * hidden parameter which is used to construct the
	 * {@link AbstractDataReader#PARAMETER_META_DATA}
	 */
	public static final String PARAMETER_COLUMN_INDEX = "column_index";

	/**
	 * hidden parameter which is used to construct the
	 * {@link AbstractDataReader#PARAMETER_META_DATA}
	 */
	public static final String PARAMETER_COLUMN_META_DATA = "attribute_meta_data_information";

	/**
	 * hidden parameter which is used to construct the
	 * {@link AbstractDataReader#PARAMETER_META_DATA}
	 */
	public static final String PARAMETER_COLUMN_NAME = "attribute name";

	/**
	 * hidden parameter which is used to construct the
	 * {@link AbstractDataReader#PARAMETER_META_DATA}
	 */
	public static final String PARAMETER_COLUMN_SELECTED = "column_selected";

	/**
	 * hidden parameter which is used to construct the
	 * {@link AbstractDataReader#PARAMETER_META_DATA}
	 */
	public static final String PARAMETER_COLUM_VALUE_TYPE = "attribute_value_type";

	/**
	 * hidden parameter which is used to construct the
	 * {@link AbstractDataReader#PARAMETER_META_DATA}
	 */
	public static final String PARAMETER_COLUM_ROLE = "attribute_role";

	public static final ArrayList<String> ROLE_NAMES = new ArrayList<String>();

	{
		ROLE_NAMES.clear();
		for (int i = 0; i < Attributes.KNOWN_ATTRIBUTE_TYPES.length; i++) {
			if (Attributes.KNOWN_ATTRIBUTE_TYPES[i].equals("attribute")) {
				ROLE_NAMES.add(AttributeColumn.REGULAR);
			} else {
				ROLE_NAMES.add(Attributes.KNOWN_ATTRIBUTE_TYPES[i]);
			}
		}
	}

	/**
	 * a list of errors which might occurred during the importing prozess.
	 */
	private List<OperatorException> importErrors = new LinkedList<OperatorException>();

	public AbstractDataReader(Class<? extends IOObject> generatedClass) {
		super(generatedClass);
	}

	protected abstract DataSet getDataSet() throws OperatorException, IOException;

	/**
	 * the row count is the number of row/lines which are read during the guessing process. It is
	 * only used for the operator's MetaData prediction.
	 *
	 * @see AbstractDataReader#guessValueTypes()
	 */
	private int rowCountFromGuessing = 0;

	/**
	 * Indicated whether the operator MetaData is only guessed ( <code>metaDataFixed == false</code>
	 * ) or somebody called {@link AbstractDataReader#fixMetaDataDefinition()}.
	 *
	 */
	private boolean metaDataFixed = false;

	/**
	 * Indicates whether the ValueTypes were already guessed once.
	 */
	private boolean guessedValueTypes = false;

	private boolean detectErrorsInPreview = false;

	/**
	 * cached flag in order to avoid reading the parameter every single row
	 */
	boolean isErrorTollerantCache = true;

	/**
	 * Flag which interrupts the reading prcocess if it is set <code>true</code> . @see
	 * {@link AbstractDataReader#stopReading()}
	 */
	private boolean stopReading = false;

	/**
	 * Flag which interrupts the reading prcocess if it is set <code>true</code> . @see
	 * {@link AbstractDataReader#stopReading()}
	 */
	protected boolean skipGuessingValueTypes = false;

	/**
	 * Data structure to manage the background highlighting for cells, which can not be parsed as
	 * the specified value type.
	 *
	 * Maps the column to a set of row which in which the parsing failed.
	 *
	 * @see AbstractDataReader#hasParseError(int, int)
	 * @see AbstractDataReader#hasParseErrorInColumn(int)
	 */
	TreeMap<Integer, TreeSet<Integer>> errorCells = new TreeMap<Integer, TreeSet<Integer>>();

	/**
	 * The columns of the created {@link ExampleSet}.
	 *
	 * @see AbstractDataReader#createExampleSet()
	 * @see AbstractDataReader#guessValueTypes()
	 */
	private List<AttributeColumn> attributeColumns = new ArrayList<AttributeColumn>();

	public void clearAllReaderSettings() {
		clearReaderSettings();
		deleteAttributeMetaDataParamters();
	}

	public void clearReaderSettings() {
		stopReading();
		attributeColumns.clear();
		importErrors.clear();
	}

	public void deleteAttributeMetaDataParamters() {
		setParameter(PARAMETER_META_DATA, null);
		setAttributeNamesDefinedByUser(false);
	}

	// public void clearOperatorMetaData() {
	// // just meta data information for the gui
	// metaDataFixed = false;
	// guessedValueTypes = false;
	// rowCountFromGuessing = 0;
	// }

	public boolean attributeNamesDefinedByUser() {
		return getParameterAsBoolean(PARAMETER_ATTRIBUTE_NAMES_DEFINED_BY_USER);
	}

	public void setAttributeNamesDefinedByUser(boolean flag) {
		setParameter(PARAMETER_ATTRIBUTE_NAMES_DEFINED_BY_USER, Boolean.toString(flag));
	}

	/**
	 * Returns all <b>activated</b> attribute columns.
	 *
	 * @return
	 */
	public List<AttributeColumn> getActiveAttributeColumns() {
		List<AttributeColumn> list = new LinkedList<AttributeColumn>();
		for (AttributeColumn column : attributeColumns) {
			if (column.isActivated()) {
				list.add(column);
			}
		}
		return list;
	}

	/**
	 * Returns all attribute columns, despite they are activated or not.
	 *
	 * @return
	 */
	public List<AttributeColumn> getAllAttributeColumns() {
		// List<AttributeColumn> list = new LinkedList<AttributeColumn>();
		// list.addAll(attributeColumns);
		// return list;
		return Collections.unmodifiableList(attributeColumns);
	}

	/**
	 * Returns the attribute column with the given index if it exists (it does not matter if the
	 * column is activated or not). Else a {@link IllegalArgumentException} is thrown
	 *
	 * @param index
	 *            the index of the requested column.
	 * @return
	 */
	public AttributeColumn getAttributeColumn(int index) throws IllegalArgumentException {
		if (index < attributeColumns.size()) {
			return attributeColumns.get(index);
		}
		throw new IllegalArgumentException("The attribute column with index " + index + " does not exists.");
	}

	/**
	 * Returns the index of the given {@link AttributeColumn} (it does not matter if it is activated
	 * or not). If the attribute column does not exist, -1 is returned.
	 *
	 * @param column
	 * @return the index of the attribute column, -1 else.
	 */
	public int getIndexOfAttributeColumn(AttributeColumn column) {
		return attributeColumns.indexOf(column);
	}

	/**
	 * Returns the index of the given <b>activated</b> {@link AttributeColumn}. Returns -1 if the
	 * column is not activated or does not exist.
	 *
	 * @param column
	 * @return
	 */
	public int getIndexOfActiveAttributeColumn(AttributeColumn column) {
		return getActiveAttributeColumns().indexOf(column);
	}

	public void addAttributeColumn() {
		String name = getNewGenericColumnName(attributeColumns.size());
		attributeColumns.add(new AttributeColumn(name));
	}

	public void addAttributeColumn(String attributeName) {
		attributeColumns.add(new AttributeColumn(attributeName));
	}

	/**
	 * Returns <code>true</code> when somebody called
	 * {@link AbstractDataReader#fixMetaDataDefinition()}. Otherwise the operator MetaData is only
	 * guessed (<code>metaDataFixed == false</code>) or
	 *
	 * @return
	 */
	public boolean isMetaDatafixed() {
		return metaDataFixed;
	}

	/**
	 * Method to declare the operators MetaData as final.
	 */
	public void fixMetaDataDefinition() {
		metaDataFixed = true;
	}

	public void writeMetaDataInParameter() {
		deleteAttributeMetaDataParamters();
		setAttributeNamesDefinedByUser(true);
		for (AttributeColumn col : getAllAttributeColumns()) {
			col.setMetaParameter();
		}
	}

	public void loadMetaDataFromParameters() {
		List<AttributeColumn> oldColumns = attributeColumns;
		attributeColumns.clear();
		try {
			List<String[]> metaData = getParameterList(PARAMETER_META_DATA);
			// first create as many attribute columns as parameters in the list
			// exists
			for (int i = 0; i < metaData.size(); i++) {
				this.addAttributeColumn();
			}
			Iterator<AttributeColumn> it = oldColumns.iterator();
			// then let them load their properties from the meta data
			for (AttributeColumn column : getAllAttributeColumns()) {
				column.loadMetaParameter();
				// restore annotations
				Annotations ann = it.next().getAnnotations();
				for (String key : ann.getKeys()) {
					column.getAnnotations().setAnnotation(key, ann.get(key));
				}
			}
		} catch (UndefinedParameterError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns the number of all columns, regardless a column is activated or not.
	 *
	 * @return
	 */
	public int getColumnCount() {
		return attributeColumns.size();
	}

	/**
	 * Returns the number of row which are read during the value type guessing.
	 *
	 * @see AbstractDataReader#guessValueTypes()
	 *
	 * @return
	 */
	private int getRowCountFromGuessing() {
		return rowCountFromGuessing;
	}

	private double[] generateRow(DataSet set, List<Attribute> activeAttributes, int rowNumber) throws OperatorException {

		List<AttributeColumn> allAttributeColumns = getAllAttributeColumns();
		if (allAttributeColumns.size() > set.getNumberOfColumnsInCurrentRow()) {
			UnexpectedRowLenghtException e = new TooShortRowLengthException(rowNumber, set.getNumberOfColumnsInCurrentRow(),
					allAttributeColumns.size());
			if (isErrorTolerant()) {
				this.logReadingError(e);
			} else {
				throw e;
			}
		}

		if (allAttributeColumns.size() < set.getNumberOfColumnsInCurrentRow()) {
			UnexpectedRowLenghtException e = new TooLongRowLengthException(rowNumber, set.getNumberOfColumnsInCurrentRow(),
					allAttributeColumns.size());
			if (isErrorTolerant()) {
				this.logReadingError(e);
			} else {
				throw e;
			}
		}
		double[] values = new double[activeAttributes.size()];

		for (int i = 0; i < values.length; i++) {
			values[i] = Double.NaN;
		}

		int activeAttributeIndex = 0;
		for (int columnIndex = 0; columnIndex < set.getNumberOfColumnsInCurrentRow(); columnIndex++) {
			AttributeColumn column = allAttributeColumns.get(columnIndex);
			// skip deactivated columns
			if (!column.isActivated()) {
				continue;
			}

			assert columnIndex != -1;

			try {
				// do Ontology.ATTRIBUTE_VALUE_TYPE.isA(..) comparisons after
				// the
				// explicit check on the value type due to performance reasons.
				if (set.isMissing(columnIndex)) {
					values[activeAttributeIndex] = Double.NaN;
				} else if (column.getValueType() == Ontology.DATE || column.getValueType() == Ontology.TIME
						|| column.getValueType() == Ontology.DATE_TIME
						|| Ontology.ATTRIBUTE_VALUE_TYPE.isA(column.getValueType(), Ontology.DATE_TIME)) {
					Date dateValue = set.getDate(columnIndex);
					if (dateValue == null) {
						throw new UnexpectedValueTypeException(Ontology.DATE_TIME, rowNumber, columnIndex,
								set.getString(columnIndex));
					}
					values[activeAttributeIndex] = dateValue.getTime();
				} else if (column.getValueType() == Ontology.INTEGER || column.getValueType() == Ontology.REAL
						|| column.getValueType() == Ontology.NUMERICAL
						|| Ontology.ATTRIBUTE_VALUE_TYPE.isA(column.getValueType(), Ontology.NUMERICAL)) {
					Number numberValue = set.getNumber(columnIndex);
					if (numberValue == null) {
						throw new UnexpectedValueTypeException(Ontology.NUMERICAL, rowNumber, columnIndex,
								set.getString(columnIndex));
					}
					values[activeAttributeIndex] = numberValue.doubleValue();
				} else if (column.getValueType() == Ontology.BINOMINAL || column.getValueType() == Ontology.NOMINAL
						|| Ontology.ATTRIBUTE_VALUE_TYPE.isA(column.getValueType(), Ontology.NOMINAL)) {
					try {
						values[activeAttributeIndex] = activeAttributes.get(activeAttributeIndex).getMapping()
								.mapString(set.getString(columnIndex));
					} catch (AttributeTypeException e) {
						if (isErrorTolerant()) {
							column.setValueType(Ontology.NOMINAL);
							Attribute att = column.createAttribute();
							for (String value : activeAttributes.get(columnIndex).getMapping().getValues()) {
								att.getMapping().mapString(value);
							}
							values[activeAttributeIndex] = att.getMapping().mapString(set.getString(columnIndex));
							activeAttributes.set(columnIndex, att);
						} else {
							throw new AttributeTypeException("Attribute: " + column.getName() + ", Index: " + columnIndex
									+ ", Row: " + rowNumber + "\n\n" + e.getMessage());
						}
					}
				} else {
					throw new OperatorException("The value type of the attribute " + column.getName() + " is unknown.");
				}

			} catch (UnexpectedValueTypeException e) {
				if (isErrorTolerant()) {
					values[activeAttributeIndex] = Double.NaN;
					this.logReadingError(e);
				} else {
					throw e;
				}
			}
			activeAttributeIndex++;
		}
		return values;
	}

	/**
	 * This method adjusts the number of columns to the given number.
	 */
	private void adjustAttributeColumnsNumbers(int newNumberOfColumns) {
		// too short
		if (getAllAttributeColumns().size() < newNumberOfColumns) {
			int actualNumberOfAttributes = getAllAttributeColumns().size();
			int numberOfNewColumns = newNumberOfColumns - actualNumberOfAttributes;
			String[] genericNames = new String[numberOfNewColumns];
			for (int i = 0; i < numberOfNewColumns; i++) {
				genericNames[i] = getNewGenericColumnName(actualNumberOfAttributes + i);
			}
			for (String name : genericNames) {
				attributeColumns.add(new AttributeColumn(name));
			}

		}
		// too long
		if (getAllAttributeColumns().size() > newNumberOfColumns) {
			List<AttributeColumn> list = new ArrayList<AttributeColumn>();
			for (int i = 0; i < newNumberOfColumns; i++) {
				list.add(getAttributeColumn(i));
			}
			attributeColumns = list;
		}
	}

	/**
	 * Sets the name of each attribute to the given name.
	 *
	 * @param newColumnNames
	 */
	protected void setAttributeNames(String[] newColumnNames) {

		adjustAttributeColumnsNumbers(newColumnNames.length);
		assert attributeColumns.size() == newColumnNames.length;

		if (attributeNamesDefinedByUser()) {
			// assume attributes names were set already by the user
			return;
		}
		List<AttributeColumn> allAttributeColumns = getAllAttributeColumns();
		String[] oldColumnNames = new String[allAttributeColumns.size()];
		int i = 0;
		for (AttributeColumn column : allAttributeColumns) {
			oldColumnNames[i] = column.getName();
			i++;
		}

		newColumnNames = getGenericColumnNames(newColumnNames, oldColumnNames);
		i = 0;
		for (AttributeColumn column : allAttributeColumns) {
			column.setName(newColumnNames[i]);
			i++;
		}
	}

	/**
	 * Resets the column names to a generic column name given by the method
	 * {@link AbstractDataReader#getNewGenericColumnName(int)}.
	 */
	protected void resetColumnNames() {
		int i = 0;
		for (AttributeColumn column : getAllAttributeColumns()) {
			column.setName(getNewGenericColumnName(i));
			i++;
		}
	}

	/**
	 *
	 */
	public void stopReading() {
		stopReading = true;

	}

	protected void setAnnotations(Annotations[] annotations) {
		assert getAllAttributeColumns().size() == annotations.length;
		int i = 0;
		for (AttributeColumn column : getAllAttributeColumns()) {
			column.getAnnotations().clear();
			column.getAnnotations().putAll(annotations[i]);
			i++;
		}
	}

	protected void setValueTypes(List<Integer> valueTypesList) throws OperatorException {
		if (getAllAttributeColumns().size() != valueTypesList.size()) {
			throw new OperatorException(
					"Internal error: The number of valueTypes does not match with the number of attributes.");
		} else {
			Iterator<Integer> it = valueTypesList.iterator();
			for (AttributeColumn column : getAllAttributeColumns()) {
				column.setValueType(it.next());
			}
		}
	}

	/**
	 * @param e
	 */
	private void logReadingError(OperatorException e) {
		importErrors.add(e);
	}


	public boolean hasParseError(int column, int row) {
		TreeSet<Integer> treeSet = errorCells.get(column);
		if (treeSet != null) {
			return treeSet.contains(row);
		}
		return false;
	}

	public boolean isDetectErrorsInPreview() {
		return detectErrorsInPreview;
	}

	public void setDetectErrorsInPreview(boolean detectErrorsInPreview) {
		this.detectErrorsInPreview = detectErrorsInPreview;
	}

	/**
	 * Returns a new column name for new column to build. Probably something like "attribute_1".
	 *
	 * @param column
	 * @return a unique column name
	 */
	protected String getNewGenericColumnName(int column) {
		HashSet<String> usedNames = new HashSet<String>();
		for (AttributeColumn col : getAllAttributeColumns()) {
			usedNames.add(col.getName());
		}

		while (usedNames.contains("attribute_" + column)) {
			column++;
		}
		return "attribute_" + column;
	}

	/**
	 * Returns a generic column name, probably something like proposedName+"_"+columnIndex.
	 *
	 * @param oldColumnNames
	 *
	 * @param proposedName
	 *            can be null, then "attribute" the proposed name is "attribute"
	 * @param columnIndex
	 *            the index of the column of this attribute.
	 * @return
	 */
	private String[] getGenericColumnNames(String[] proposedNames, String[] oldColumnNames) {
		HashSet<String> usedNames = new HashSet<String>();
		for (AttributeColumn col : getAllAttributeColumns()) {
			usedNames.add(col.getName());
		}

		int offset = usedNames.size();
		String[] genericNames = new String[proposedNames.length];
		for (int i = 0; i < proposedNames.length; i++) {
			String proposedName = proposedNames[i];
			if (proposedName == null) {
				proposedName = "attribute_" + (offset + i + 1);
			}
			if (!proposedName.equals(oldColumnNames[i])) {
				if (usedNames.contains(proposedName)) {
					proposedName = proposedName + "_" + (offset + i + 1);
				}
				usedNames.add(proposedName);
			}
			genericNames[i] = proposedName;
		}
		return genericNames;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see io.sugo.pio.operator.io.AbstractReader#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = new LinkedList<ParameterType>();
		types.addAll(super.getParameterTypes());

		types.add(new ParameterTypeBoolean(PARAMETER_ERROR_TOLERANT,
				"Values which does not match to the specified value typed are considered as missings.", true));

		// The meta data parameters which holds the information about the
		// attribute/column properties, eg. name, role, value type ...
		String[] roles = new String[ROLE_NAMES.size()];
		for (int i = 0; i < roles.length; i++) {
			roles[i] = ROLE_NAMES.get(i);
		}
		// hidden param
		ParameterTypeList typeList = new ParameterTypeList(PARAMETER_META_DATA, "The meta data information",
				new ParameterTypeInt(PARAMETER_COLUMN_INDEX, "The column index", 0, 9999), //
				new ParameterTypeTupel(PARAMETER_COLUMN_META_DATA, "the meta data information of one column", //
						new ParameterTypeString(PARAMETER_COLUMN_NAME, "Describes the attributes name.", ""), //
						new ParameterTypeBoolean(PARAMETER_COLUMN_SELECTED, "Indicates if a column is selected", true), //
						new ParameterTypeCategory(PARAMETER_COLUM_VALUE_TYPE, "Indicates the value type of an attribute",
								Ontology.VALUE_TYPE_NAMES, Ontology.NOMINAL), //
						new ParameterTypeStringCategory(PARAMETER_COLUM_ROLE, "Indicates the role of an attribute", roles,
								AttributeColumn.REGULAR)));
		typeList.setHidden(true);
		types.add(typeList);

		// hidden param
		ParameterTypeBoolean typeBool = new ParameterTypeBoolean(PARAMETER_ATTRIBUTE_NAMES_DEFINED_BY_USER,
				"the parameter describes whether the attribute names were set by the user manually or were generated by the the reader (generic names or first row of the file)",
				false);
		types.add(typeBool);
		return types;
	}

	/**
	 * Use this method to set the parameter {@link AbstractDataReader#PARAMETER_ERROR_TOLERANT}. Do
	 * not set the parameter directly because its value need to be cached.
	 *
	 * @param flag
	 */
	public void setErrorTolerant(boolean flag) {
		isErrorTollerantCache = flag;
		setParameter(PARAMETER_ERROR_TOLERANT, Boolean.toString(flag));
	}

	/**
	 * @return the cached value of the parameter {@link AbstractDataReader#PARAMETER_ERROR_TOLERANT}
	 *         . The parameter needs to be cached since it cost to much time to read the parameter
	 *         every line.
	 */
	public boolean isErrorTolerant() {
		return isErrorTollerantCache;
	}


	private abstract class UnexpectedRowLenghtException extends OperatorException {

		private static final long serialVersionUID = 1L;

		private int rowNumber = -1;
		private int rowLenght = -1;
		int expectedRowLenght = -1;

		/**
		 *
		 */
		public UnexpectedRowLenghtException(String message, int rowNumber, int rowLenght, int expectedRowLenght) {
			super(message);
			this.rowNumber = rowNumber;
			this.rowLenght = rowLenght;
			this.expectedRowLenght = expectedRowLenght;
		}

		/**
		 *
		 */
		public UnexpectedRowLenghtException(int rowNumber, int rowLenght, int expectedRowLenght) {
			super("NO MESSAGE");
			this.rowNumber = rowNumber;

		}

		/**
		 * Returns the row where the error occurred. <b>Warning:</b> you might want to add +1 if you
		 * intend to present this number to the user.
		 *
		 *
		 * @return
		 */
		public int getRow() {
			return rowNumber;
		}

		/**
		 * Returns the length of the the row {@link UnexpectedRowLenghtException#rowNumber}
		 *
		 * @return
		 */
		public int getRowLenght() {
			return rowLenght;
		}

		public int getExpectedRowLenght() {
			return expectedRowLenght;
		}
	}

	public class TooShortRowLengthException extends UnexpectedRowLenghtException {

		private static final long serialVersionUID = -9183147637149034838L;

		/**
		 * @param rowNumber
		 * @param rowLenght
		 * @param expectedRowLenght
		 */
		public TooShortRowLengthException(int rowNumber, int rowLenght, int expectedRowLenght) {
			super("Row number <b>" + rowNumber + "<//b> is too <b>short<//b>. The row has <b>" + rowLenght
					+ "<//b> columns but it is expected to have <b>" + expectedRowLenght + "<//b> columns.", rowNumber,
					rowLenght, expectedRowLenght);
		}
	}

	public class TooLongRowLengthException extends UnexpectedRowLenghtException {

		private static final long serialVersionUID = -9079042758212112074L;

		/**
		 * @param rowNumber
		 * @param rowLenght
		 * @param expectedRowLenght
		 */
		public TooLongRowLengthException(int rowNumber, int rowLenght, int expectedRowLenght) {
			super("Row number <b>" + rowNumber + "</b> is too <b>long</b>. It has <b>" + rowLenght
					+ "</b> columns but it is expected to have <b>" + expectedRowLenght + "</b> columns.", rowNumber,
					rowLenght, expectedRowLenght);
		}
	}

	public static class UnexpectedValueTypeException extends OperatorException {

		private static final long serialVersionUID = 1L;
		private int expectedValueType = -1;
		private int row = -1;
		private int column = -1;
		private Object value = null;

		public UnexpectedValueTypeException(String message, int expectedValueType, int column, int row, Object value) {
			super(message);
			this.expectedValueType = expectedValueType;
			this.row = row;
			this.column = column;
			this.value = value;
		}

		/**
		 * Creates a proper error message;
		 *
		 * @param expectedValueType
		 * @param column
		 * @param row
		 * @param value
		 */
		public UnexpectedValueTypeException(int expectedValueType, int column, int row, Object value) {
			this("Could not interpreted the value <b>" + value + "<//b> in row <b>" + row + "<//b> and column <b>" + column
					+ "<//b> as a <b>" + Ontology.ATTRIBUTE_VALUE_TYPE.mapIndex(expectedValueType)
					+ "<//b>. Plaese adjust to a proper value type or enable the operator's error tolerance.",
					expectedValueType, column, row, value);
		}

		/**
		 * Returns the row where the error occurred. <b>Warning:</b> you might want to add +1 if you
		 * intend to present this number to the user.
		 *
		 * @return
		 */
		public int getRow() {
			return row;
		}

		/**
		 * Returns the column where the error occurred. <b>Warning:</b> you might want to add +1 if
		 * you intend to present this number to the user.
		 *
		 * @return
		 */
		public int getColumn() {
			return column;
		}

		/**
		 * Returns the value which caused the error
		 *
		 * @return
		 */
		public Object getValue() {
			return value;
		}

		/**
		 * @return the expectedValueType
		 */
		public int getExpectedValueType() {
			return expectedValueType;
		}
	}

	/**
	 * @author Sebastian Loh (28.04.2010)
	 *
	 *         <p>
	 *         Private class describing a column of the created ExampleSet. Holds all information
	 *         (name, value type, annotations) in order to create the actual attribute for this
	 *         column. Despite that the class manages different properties - eg. the activation
	 *         status (is the column actual selected to be read?) and missing values - in order to
	 *         build proper meta data description.
	 *         </p>
	 *
	 * @see AbstractDataReader#getGeneratedMetaData()
	 * @see AbstractDataReader#guessValueTypes()
	 * @see AbstractDataReader#createExampleSet(ExampleSetMetaData, int)
	 *
	 */
	public class AttributeColumn {

		public static final int NAME_PARAMETER = 0;

		public static final int IS_ACTIVATED_PARAMETER = 1;

		public static final int VALUE_TYPE_PARAMETER = 2;

		public static final int ROLE_PARAMETER = 3;

		@Override
		public String toString() {
			return name + "," + isActivated + "," + valueType + "," + role + "," + annotations;
		}

		/**
		 * ugly workaround to define regular role instead of role = null;
		 */
		public static final String REGULAR = "regular";

		private String name;

		private String role = REGULAR;

		private boolean isActivated = true;

		private int valueType = Ontology.NOMINAL;

		private Attribute attribute = null;

		/**
		 * the column's annotations that are also the attribute's annotations, which is created from
		 * this column.
		 */
		private Annotations annotations = new Annotations();

		/**
		 * The minValue of this attribute. Only for the operator MetaData purposes.
		 */
		protected double minValue = Double.NEGATIVE_INFINITY;

		/**
		 * The maxValue of this attribute. Only for the operator MetaData purposes.
		 */
		protected double maxValue = Double.POSITIVE_INFINITY;

		/**
		 * The valueSet of this attribute, in case it is (bi)nominal. Only for the operator MetaData
		 * purposes.
		 */
		protected Set<String> valueSet = new LinkedHashSet<String>();

		/**
		 * The number of missing values which were read during the guessing. Only for the operator
		 * MetaData purposes.
		 */
		protected int numberOfMissings = 0;

		/**
		 * indicate whether this attribute is a candidate for value type real
		 */
		private boolean canParseDouble = true;

		/**
		 * indicate whether this attribute is a candidate for value type integer
		 */
		private boolean canParseInteger = true;

		/**
		 * indicate whether this attribute is a candidate for value type date
		 */
		private boolean canParseDate = true;

		/**
		 * indicate whether this attribute is a candidate for value type date without time
		 */
		private boolean shouldBeDate = false;

		/**
		 * indicate whether this attribute is a candidate for value type only time
		 */
		private boolean shouldBeTime = false;

		/**
		 * the last date which was read, to guess if it is date or date/time or both
		 */
		private Date lastDate = null;

		/**
		 * increase the number of read missing value by one.
		 *
		 * @return the number after the increasement.
		 */
		public int incNummerOfMissing() {
			return numberOfMissings++;
		}

		/**
		 * @return
		 */
		public Annotations getAnnotations() {
			return annotations;
		}

		/**
		 * Creates the actual attribute object that is described by this column's properties (name,
		 * value type, annotations).
		 *
		 * @return the created attribute.
		 */
		private Attribute createAttribute() {
			Attribute att = AttributeFactory.createAttribute(getName(), getValueType());
			attribute = att;
			return att;
		}

		/**
		 * @return the columns {@link Attribute}. If a attribute was not created before, a new
		 *         Attribute is created. Otherwise a new Attribute is created if the already
		 *         existing Attribute does not match to the current AttributeColumn settings.
		 */
		public Attribute getAttribute() {
			if (attribute == null) {
				return createAttribute();
			}
			if (!attribute.getName().equals(getName())) {
				return createAttribute();
			}
			if (attribute.getValueType() != this.getValueType()) {
				return createAttribute();
			}
			// check same annotations
//			for (String key : this.getAnnotations().getKeys()) {
//				if (!attribute.getAnnotations().get(key).equals(this.getAnnotations().get(key))) {
//					return createAttribute();
//				}
//			}
			// else the attribute information are equal:
			return attribute;
		}

		/**
		 * Indicated whether this column is actual read/imported or if it is ignored. In other word,
		 * returns <code>true</code> if the column is active
		 */
		public boolean isActivated() {
			return isActivated;
		}

		/**
		 * Activates or deactivates this column.
		 *
		 * @param flag
		 */
		public void activateColumn(boolean flag) {
			isActivated = flag;
		}

		/**
		 * Returns the value type of the columns attribute.
		 *
		 * @see Ontology
		 *
		 * @return
		 */
		public int getValueType() {
			return valueType;
		}

		/**
		 * Sets the value type of the columns attribute by actually replacing the existing attribute
		 * with a new generated attribute with same name and the new type.
		 *
		 * @param newValueType
		 */
		public void setValueType(int newValueType) {
			valueType = newValueType;
		}

		/**
		 * Returns the name of this column, which is also the name of the attribute that is created
		 * from this column's properties.
		 *
		 * @return the name
		 */
		public String getName() {
			return name;
			//
		}

		/**
		 * @param name
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Returns the attribute's role as a String.
		 *
		 * @return
		 */
		public String getRole() {
			return role;
		}

		/**
		 * Set the role of the attribute column
		 *
		 * @param role
		 */
		public void setRole(String role) {
			this.role = role;
		}

		private String getMetaParameter(int parameterIndex) {
			int index = getIndexOfAttributeColumn(this);

			try {
				// get former parameters
				List<String[]> list = getParameterList(PARAMETER_META_DATA);

				String[] metadata;
				String tuple;
				String[] map = null;
				// get the metadata of this attribute if exists
				for (String[] m : list) {
					if (Integer.parseInt(m[0]) == index) {
						map = m;
						break;
					}
				}
				if (map == null) {
					return null;
				}

				tuple = map[1];
				metadata = ParameterTypeTupel.transformString2Tupel(tuple);

				// return the parameter
				return metadata[parameterIndex];
			} catch (UndefinedParameterError e) {
				e.printStackTrace();
				return null;
			}
		}

		private void loadMetaParameter() {
			if (getMetaParameter(NAME_PARAMETER) != null) {
				setName(getMetaParameter(NAME_PARAMETER));
			}
			String s = getMetaParameter(VALUE_TYPE_PARAMETER);
			if (s != null) {
				setValueType(Integer.parseInt(s));
			}
			if (getMetaParameter(ROLE_PARAMETER) != null) {
				setRole(getMetaParameter(ROLE_PARAMETER));
			}
			if (getMetaParameter(IS_ACTIVATED_PARAMETER) != null) {
				activateColumn(Boolean.parseBoolean(getMetaParameter(IS_ACTIVATED_PARAMETER)));
			}

		}

		/**
		 *
		 *
		 * Sets the meta data value with entry <code>parameterIndex</code> of this
		 * {@link AttributeColumn}.
		 * <p>
		 * If this method is called the first time for this attributeColumn, default parameters are
		 * set. (the first time means that this attribute column does not have an index, ie.
		 * <code>getIndexOfAttributeColumn(this) == -1</code>.
		 * </p>
		 *
		 * @param parameterIndex
		 *            legal parameters are: {@link AttributeColumn#NAME_PARAMETER},
		 *            {@link AttributeColumn#IS_ACTIVATED_PARAMETER},
		 *            {@link AttributeColumn#VALUE_TYPE_PARAMETER},
		 *            {@link AttributeColumn#ROLE_PARAMETER}.
		 *
		 * @param value
		 *            the new value
		 */
		private void setMetaParameter() {
			// get index of this column
			int myIndex = getIndexOfAttributeColumn(this);

			try {
				List<String[]> list = getParameterList(PARAMETER_META_DATA);
				String[] map = null;
				for (String[] mapIndexToValues : list) {
					if (Integer.parseInt(mapIndexToValues[0]) == myIndex) {
						map = mapIndexToValues;
						break;
					}
				}
				String[] metadata;
				String tuple;
				// if an entry for this attribute column did not exist, create a
				// new one:
				if (map == null) {
					map = new String[2];
					map[0] = Integer.toString(myIndex);
					list.add(map);
					metadata = new String[4];
				} else {
					tuple = map[1];
					metadata = ParameterTypeTupel.transformString2Tupel(tuple);
				}
				// create new entries with default values
				metadata[NAME_PARAMETER] = name;
				// selection is true
				metadata[IS_ACTIVATED_PARAMETER] = Boolean.toString(isActivated);
				// value type is nominal
				metadata[VALUE_TYPE_PARAMETER] = Integer.toString(valueType);
				// role is regular
				metadata[ROLE_PARAMETER] = role;

				// write everything back
				tuple = ParameterTypeTupel.transformTupel2String(metadata);
				map[1] = tuple;

				// list.set(index, map);

				// store modified metadata in the parameter;
				String entry = ParameterTypeList.transformList2String(list);
				setParameter(PARAMETER_META_DATA, entry);
			} catch (UndefinedParameterError e) {
				e.printStackTrace();
			}
		}

		/**
		 * creates a new column and generated a attribute with the given name and nominal value type
		 *
		 * @param attributeName
		 */
		public AttributeColumn(String attributeName) {
			// default parameters for value type, ... are implicit created
			this.setName(attributeName);
			// this.setValueType(Ontology.NOMINAL);
		}
	}

	protected abstract class DataSet {

		/**
		 * Proceed to the next row if existent. Should return true if such a row exists or false, if
		 * no such next row exists.
		 *
		 * @return
		 */
		public abstract boolean next();

		/**
		 * Returns the number of columns in the current row, i.e. the length of the row.
		 *
		 * @return
		 */
		public abstract int getNumberOfColumnsInCurrentRow();

		/**
		 * Returns whether the value in the specified column in the current row is missing.
		 *
		 * @param columnIndex
		 *            index of the column
		 * @return
		 */
		public abstract boolean isMissing(int columnIndex);

		/**
		 * Returns a numerical value contained in the specified column in the current row. Should
		 * return null if the value is not a numerical or if the value is missing.
		 *
		 * @param columnIndex
		 * @return
		 */
		public abstract Number getNumber(int columnIndex);

		/**
		 * Returns a nominal value contained in the specified column in the current row. Should
		 * return null if the value is not a nominal or a kind of string type or if the value is
		 * missing.
		 *
		 * @param columnIndex
		 * @return
		 */
		public abstract String getString(int columnIndex);

		/**
		 * Returns a date, time or date_time value contained in the specified column in the current
		 * row. Should return null if the value is not a date or time value or if the value is
		 * missing.
		 *
		 * @param columnIndex
		 * @return
		 */
		public abstract Date getDate(int columnIndex);

		/**
		 * Closes the data source. May tear down a database connection or close a file which is re`
		 * from.
		 *
		 * @throws OperatorException
		 */
		public abstract void close() throws OperatorException;
	}

}
