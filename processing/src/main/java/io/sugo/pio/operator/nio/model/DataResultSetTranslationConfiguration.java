package io.sugo.pio.operator.nio.model;

import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.table.DataRowFactory;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.io.ExampleSource;
import io.sugo.pio.parameter.ParameterTypeList;
import io.sugo.pio.parameter.ParameterTypeTupel;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.ports.metadata.MDInteger;
import io.sugo.pio.tools.Ontology;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import static io.sugo.pio.operator.nio.model.AbstractDataResultSetReader.PARAMETER_META_DATA;


/**
 * This class holds information how a DataResultSet is translated into an ExampleSet. Therefore it
 * holds information about the final name, the value type, role and if the column is selected at
 * all.
 *
 */
public class DataResultSetTranslationConfiguration {

    public static final String PARAMETER_DATAMANAGEMENT = "datamanagement";
    public static final String PARAMETER_FIRST_ROW_AS_NAMES = "first_row_as_names";
    public static final String PARAMETER_ANNOTATIONS = "annotations";
    public static final String ANNOTATION_NAME = "Name";
    public static final String PARAMETER_DATE_FORMAT = "date_format";
    public static final String PARAMETER_TIME_ZONE = "time_zone";

    private ColumnMetaData[] columnMetaData;

    private Locale locale = Locale.getDefault();
    private String datePattern = "";

    private final SortedMap<Integer, String> annotationsMap = new TreeMap<>();
    private boolean faultTolerant = true;

    private ThreadLocal<DateFormat> dateFormat;

    private NumberFormat numberFormat;

    private int dataManagementType = DataRowFactory.TYPE_DOUBLE_ARRAY;

    /**
     * This constructor can be used to generate an empty configuration just depending on the given
     * resultSet
     *
     * @param readerOperator
     * @throws OperatorException
     */
    public DataResultSetTranslationConfiguration(AbstractDataResultSetReader readerOperator) {
        this(readerOperator, null);
    }

    /**
     * This constructor can be used to generate a configuration with the given annotations.
     *
     * @param dataResultSet the result set to derive parameters from
     * @param annotations   list of annotations for the rows
     */
    public DataResultSetTranslationConfiguration(DataResultSet dataResultSet, List<String> annotations) {
        this(null, dataResultSet);
        int counter = 0;
        annotationsMap.clear();
        for (String annotation : annotations) {
            if (annotation != null) {
                annotationsMap.put(counter, annotation);
            }
            counter++;
        }
    }

    /**
     * Creates the configuration based on the parameter values stored in the given reader. If these
     * parameters aren't present they are derived from the data result set delivered and everything
     * will just be passed. This means, names are identically as delivered from the underlying
     * result set, value type will be the one fitting, everything is selected, no roles are defined.
     *
     * @throws OperatorException
     */
    private DataResultSetTranslationConfiguration(AbstractDataResultSetReader readerOperator, DataResultSet dataResultSet) {
        reconfigure(dataResultSet);
        reconfigure(readerOperator);
    }

    public void reconfigure(AbstractDataResultSetReader readerOperator) {
        // reading parameter settings
        if (readerOperator != null) {
            try {
                dataManagementType = readerOperator.getParameterAsInt(ExampleSource.PARAMETER_DATAMANAGEMENT);
            } catch (UndefinedParameterError e1) {
                dataManagementType = DataRowFactory.TYPE_DOUBLE_ARRAY;
            }

            boolean firstRowAsNames = readerOperator.getParameterAsBoolean(PARAMETER_FIRST_ROW_AS_NAMES);
            if (firstRowAsNames) {
                annotationsMap.put(0, ANNOTATION_NAME);
            } else {
                List<String[]> annotations;
                try {
                    annotations = readerOperator.getParameterList(PARAMETER_ANNOTATIONS);
                } catch (UndefinedParameterError e) {
                    annotations = Collections.emptyList();
                }
                for (String[] annotation : annotations) {
                    annotationsMap.put(Integer.parseInt(annotation[0]), annotation[1]);
                }
            }

            // reading date format settings
            try {
                setDatePattern(readerOperator.getParameterAsString(PARAMETER_DATE_FORMAT));
            } catch (UndefinedParameterError e) {
                setDatePattern("");
            }

            /*try {
                int localeIndex;
                localeIndex = readerOperator.getParameterAsInt(PARAMETER_LOCALE);
                if (localeIndex >= 0 && localeIndex < AbstractDateDataProcessing.availableLocales.size()) {
                    locale = AbstractDateDataProcessing.availableLocales.get(localeIndex);
                }
            } catch (UndefinedParameterError e) {
                locale = Locale.getDefault();
            }*/
            locale = Locale.getDefault();

            columnMetaData = readColumnMetaData(readerOperator);
            setFaultTolerant(readerOperator.getParameterAsBoolean(AbstractDataResultSetReader.PARAMETER_ERROR_TOLERANT));
        } else {
            annotationsMap.put(0, ANNOTATION_NAME);
        }
    }

    public void setColumnMetaData(ColumnMetaData[] columnMetaData) {
        this.columnMetaData = columnMetaData;
    }

    public void reconfigure(DataResultSet dataResultSet) {
        if (dataResultSet != null) {
            int numberOfColumns = dataResultSet.getNumberOfColumns();
            columnMetaData = new ColumnMetaData[numberOfColumns];
            final String[] originalColumnNames = dataResultSet.getColumnNames();
            int[] attributeValueTypes = dataResultSet.getValueTypes();
            for (int i = 0; i < numberOfColumns; i++) {
                columnMetaData[i] = new ColumnMetaData(originalColumnNames[i], originalColumnNames[i],
                        attributeValueTypes[i], Attributes.ATTRIBUTE_NAME, true);
            }
        }
    }

    /**
     * Sets the parameters in the given operator to describe this configuration.
     */
    public void setParameters(AbstractDataResultSetReader operator) {
        operator.getParameters().setParameter(PARAMETER_DATE_FORMAT, getDatePattern());
        // meta data
        List<String[]> metaDataList = new LinkedList<>();
        int index = 0;
        for (ColumnMetaData cmd : getColumnMetaData()) {
            String[] tupel = new String[4];
            tupel[0] = cmd.getUserDefinedAttributeName();
            tupel[1] = String.valueOf(cmd.isSelected());
            tupel[2] = Ontology.ATTRIBUTE_VALUE_TYPE.mapIndex(cmd.getAttributeValueType());
            tupel[3] = cmd.getRole();
			String encodedTupel = ParameterTypeTupel.transformTupel2String(tupel);
			metaDataList.add(new String[] { String.valueOf(index), encodedTupel });
            index++;
        }
		operator.getParameters().setParameter(PARAMETER_META_DATA, ParameterTypeList.transformList2String(metaDataList));

        // annotations
        List<String[]> annotationList = new LinkedList<>();
        for (Entry<Integer, String> annotation : annotationsMap.entrySet()) {
            annotationList.add(new String[]{annotation.getKey().toString(), annotation.getValue()});
        }
        operator.setParameter(PARAMETER_ANNOTATIONS, ParameterTypeList.transformList2String(annotationList));

		operator.getParameters().setParameter(AbstractDataResultSetReader.PARAMETER_ERROR_TOLERANT,
				String.valueOf(isFaultTolerant()));
        operator.getParameters().setParameter(PARAMETER_FIRST_ROW_AS_NAMES, "false");
    }

    public ColumnMetaData getColumnMetaData(int col) {
        if (columnMetaData != null && col < columnMetaData.length) {
            return columnMetaData[col];
        } else {
            return null;
        }
    }

    /**
     * This will return all indices of each selected column
     */
    public int[] getSelectedIndices() {
        int numberOfSelected = 0;
        int[] selectedIndices = new int[columnMetaData.length];
        for (int i = 0; i < selectedIndices.length; i++) {
            if (columnMetaData[i].isSelected()) {
                selectedIndices[numberOfSelected] = i;
                numberOfSelected++;
            }
        }
        if (numberOfSelected < selectedIndices.length) {
            int[] result = new int[numberOfSelected];
            System.arraycopy(selectedIndices, 0, result, 0, numberOfSelected);
            return result;
        } else {
            return selectedIndices;
        }
    }

    /**
     * This returns the annotation of a line or null if no present
     */
    public String getAnnotation(int line) {
        return annotationsMap.get(line);
    }

    public SortedSet<Integer> getAnnotatedRowIndices() {
        SortedSet<Integer> result = new TreeSet<>();
        result.addAll(annotationsMap.keySet());
        return result;
    }

    public Map<Integer, String> getAnnotationsMap() {
        return annotationsMap;
    }

    public int getNumerOfColumns() {
        return columnMetaData.length;
    }

    public ColumnMetaData[] getColumnMetaData() {
        return columnMetaData;
    }

    public void setFaultTolerant(boolean faultTolerant) {
        this.faultTolerant = faultTolerant;
    }

    public boolean isFaultTolerant() {
        return faultTolerant;
    }

    public int getLastAnnotatedRowIndex() {
        if (annotationsMap == null || annotationsMap.isEmpty()) {
            return -1;
        }
        SortedSet<Integer> annotatedRows = getAnnotatedRowIndices();
        return annotatedRows.last();
    }

    public void resetValueTypes() {
        for (ColumnMetaData cmd : columnMetaData) {
            cmd.setAttributeValueType(Ontology.ATTRIBUTE_VALUE);
        }
    }

    public DateFormat getDateFormat() {
        if (dateFormat == null) {
            this.dateFormat = new ThreadLocal<DateFormat>() {

                @Override
                protected DateFormat initialValue() {
                    if (datePattern == null || datePattern.trim().isEmpty()) {
                        // clone because getDateInstance uses an internal pool which can return the
                        // same instance for multiple threads
                        return (DateFormat) DateFormat.getDateTimeInstance().clone();
                    } else {
                        return new SimpleDateFormat(getDatePattern(), locale);
                    }
                }
            };
        }
        return this.dateFormat.get();
    }

    public String getDatePattern() {
        return datePattern;
    }

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
        dateFormat = null;
    }

    @Override
    public String toString() {
        return "Annotations: " + annotationsMap + "; columns: " + Arrays.toString(columnMetaData);
    }

    public void addColumnMetaData(ExampleSetMetaData emd) {
        MDInteger numberOfExamples = emd.getNumberOfExamples();
        numberOfExamples.subtract(annotationsMap.size());
        for (ColumnMetaData cmd : columnMetaData) {
            if (cmd.isSelected()) {
                emd.addAttribute(cmd.getAttributeMetaData());
            }
        }
    }

    /**
     * Returns true if meta data is manually set.
     */
    public boolean isComplete() {
        return columnMetaData != null && columnMetaData.length > 0;
    }

    public void setNumberFormat(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    public int getDataManagementType() {
        return dataManagementType;
    }

    /**
     * @param readerOperator
     *            the operator to retrieve the parameter from
     */
    public static ColumnMetaData[] readColumnMetaData(AbstractDataResultSetReader readerOperator) {
        // initializing data structures
        List<String[]> metaDataSettings;
        if (readerOperator.isParameterSet(PARAMETER_META_DATA)) {
            try {
                metaDataSettings = readerOperator.getParameterList(PARAMETER_META_DATA);
            } catch (UndefinedParameterError e) {
                metaDataSettings = Collections.emptyList();
            }
        } else {
            metaDataSettings = Collections.emptyList();
        }

        // find largest used column index
        int maxUsedColumnIndex = -1;
        for (String[] metaDataDefinition : metaDataSettings) {
            int columnIndex = Integer.parseInt(metaDataDefinition[0]);
            maxUsedColumnIndex = Math.max(maxUsedColumnIndex, columnIndex);
        }
        // initialize with values from settings
        ColumnMetaData[] columnMetaData = new ColumnMetaData[maxUsedColumnIndex + 1];
        for (String[] metaDataDefinition : metaDataSettings) {
            int currentColumn = Integer.parseInt(metaDataDefinition[0]);
            String[] metaDataDefintionValues = ParameterTypeTupel.transformString2Tupel(metaDataDefinition[1]);
            columnMetaData[currentColumn] = new ColumnMetaData();
            final ColumnMetaData cmd = columnMetaData[currentColumn];
            cmd.setSelected(Boolean.parseBoolean(metaDataDefintionValues[1]));
            if (cmd.isSelected()) { // otherwise details don't matter
                cmd.setRole(metaDataDefintionValues[3].trim());
                cmd.setUserDefinedAttributeName(metaDataDefintionValues[0].trim());

                int valueType = Ontology.ATTRIBUTE_VALUE_TYPE.mapName(metaDataDefintionValues[2]);
                // fallback for old processes where attribute value type was saved as index
                // rather than as string
                if (valueType == -1) {
                    cmd.setAttributeValueType(Integer.parseInt(metaDataDefintionValues[2]));
                } else {
                    cmd.setAttributeValueType(valueType);
                }
            }
        }
        // replace those which were not specified in the list by an empty ColumnMetaData (so it
        // is at least not null)
        for (int i = 0; i < columnMetaData.length; i++) {
            if (columnMetaData[i] == null) {
                columnMetaData[i] = new ColumnMetaData();
            }
        }
        return columnMetaData;
    }

}
