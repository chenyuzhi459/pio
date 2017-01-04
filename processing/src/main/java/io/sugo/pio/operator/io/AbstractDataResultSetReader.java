package io.sugo.pio.operator.io;

import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.table.DataRowFactory;
import io.sugo.pio.operator.Annotations;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.OperatorDescription;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.io.AbstractReader;
import io.sugo.pio.operator.io.DataResultSet;
import io.sugo.pio.operator.io.DataResultSetFactory;
import io.sugo.pio.operator.io.DataResultSetTranslationConfiguration;
import io.sugo.pio.operator.io.csv.CSVResultSetConfiguration;
import io.sugo.pio.parameter.*;
import io.sugo.pio.parameter.conditions.BooleanParameterCondition;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.Tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static io.sugo.pio.operator.io.DataResultSetTranslationConfiguration.*;


/**
 * This class uses DataResultSets to load data from file and then delivers the data as an example
 * set.
 *
 * @author Sebastian Land
 */
public abstract class AbstractDataResultSetReader extends AbstractReader<ExampleSet> {

    public static final String PARAMETER_ANNOTATIONS = "annotations";

    /**
     * This parameter holds the hole information about the attribute columns. I.e. which attributes
     * are defined, the names, what value type they have, whether the att. is selected,
     */
    public static final String PARAMETER_META_DATA = "data_set_meta_data_information";
    public static final String PARAMETER_COLUMN_INDEX = "column_index";
    public static final String PARAMETER_COLUMN_META_DATA = "attribute_meta_data_information";
    public static final String PARAMETER_COLUMN_NAME = "attribute name";
    public static final String PARAMETER_COLUMN_SELECTED = "column_selected";
    public static final String PARAMETER_COLUMN_VALUE_TYPE = "attribute_value_type";
    public static final String PARAMETER_COLUMN_ROLE = "attribute_role";

    public static final String PARAMETER_ATTRIBUTES = "attributes";

//    private InputPort fileInputPort = getInputPorts().createPort("file");

//    public InputPort getFileInputPort() {
//        return fileInputPort;
//    }

    public AbstractDataResultSetReader(Class<? extends IOObject> generatedClass, String name, OutputPort outputPort){
        super(generatedClass, name, outputPort);
    }

    @Override
    public ExampleSet read() {
        // loading data result set
        ExampleSet exampleSet = null;
        try (DataResultSetFactory dataResultSetFactory = getDataResultSetFactory();
             DataResultSet dataResultSet = dataResultSetFactory.makeDataResultSet(this)) {
            try {
                exampleSet = transformDataResultSet(dataResultSet);
            } catch (OperatorException e) {
                throw new RuntimeException(e);
            }
        }
        return exampleSet;
    }

    protected abstract DataResultSetFactory getDataResultSetFactory();

    /**
     * Returns the configured number format or null if a default number format should be used.
     */
    protected abstract NumberFormat getNumberFormat() throws OperatorException;

    /**
     * Returns the allowed file extension.
     */
    protected abstract String getFileExtension();

    /**
     * Returns the allowed file extensions.
     */
    protected String[] getFileExtensions() {
        return new String[]{getFileExtension()};
    }

    /**
     * Transforms the provided {@link DataResultSet} into an example set.
     *
     * @param dataResultSet the data result set to transform into an example set
     * @return the generated example set
     * @throws OperatorException in case something goes wrong
     */
    protected ExampleSet transformDataResultSet(DataResultSet dataResultSet) throws OperatorException {

        // loading configuration
        DataResultSetTranslationConfiguration configuration = new DataResultSetTranslationConfiguration(this);
        final boolean configComplete = !configuration.isComplete();
        if (configComplete) {
            configuration.reconfigure(dataResultSet);
        }

        // now use translator to read, translate and return example set
        DataResultSetTranslator translator = new DataResultSetTranslator(this);
        NumberFormat numberFormat = getNumberFormat();
        if (numberFormat != null) {
            configuration.setNumberFormat(numberFormat);
        }

        if (configComplete) {
            translator.guessValueTypes(configuration, dataResultSet, 3);
        }
        return translator.read(dataResultSet, configuration, false);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new LinkedList<ParameterType>();

        if (isSupportingFirstRowAsNames()) {
            types.add(new ParameterTypeBoolean(
                    PARAMETER_FIRST_ROW_AS_NAMES,
                    "Indicates if the first row should be used for the attribute names. If activated no annotations can be used.",
                    true));
        }

        List<String> annotations = new LinkedList<>();
        annotations.add(ANNOTATION_NAME);
        annotations.addAll(Arrays.asList(Annotations.ALL_KEYS_ATTRIBUTE));
        ParameterType type = new ParameterTypeList(PARAMETER_ANNOTATIONS, "Maps row numbers to annotation names.", //
                new ParameterTypeInt("row_number", "Row number which contains an annotation", 0, Integer.MAX_VALUE), //
                new ParameterTypeCategory("annotation", "Name of the annotation to assign this row.",
                        annotations.toArray(new String[annotations.size()]), 0));
        if (isSupportingFirstRowAsNames()) {
            type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_FIRST_ROW_AS_NAMES, false, false));
        }
        types.add(type);

        type = new ParameterTypeDateFormat(PARAMETER_DATE_FORMAT,
                "The parse format of the date values, for example \"yyyy/MM/dd\".", "yyyy/MM/dd");
        types.add(type);

        type = new ParameterTypeCategory(PARAMETER_TIME_ZONE,
                "The time zone used for the date objects if not specified in the date string itself.",
                Tools.getAllTimeZones(), Tools.getPreferredTimeZoneIndex());
        types.add(type);

//        type = new ParameterTypeCategory(PARAMETER_LOCALE,
//                "The used locale for date texts, for example \"Wed\" (English) in contrast to \"Mi\" (German).",
//                AbstractDateDataProcessing.availableLocaleNames, AbstractDateDataProcessing.defaultLocale);
//        types.add(type);

        types.addAll(super.getParameterTypes());

        type = new ParameterTypeList(PARAMETER_META_DATA, "The meta data information", //
                new ParameterTypeInt(PARAMETER_COLUMN_INDEX, "The column index", 0, Integer.MAX_VALUE), //
                new ParameterTypeTupel(PARAMETER_COLUMN_META_DATA, "The meta data definition of one column", //
                        new ParameterTypeString(PARAMETER_COLUMN_NAME, "Describes the attributes name.", ""), //
                        new ParameterTypeBoolean(PARAMETER_COLUMN_SELECTED, "Indicates if a column is selected", true), //
                        new ParameterTypeCategory(PARAMETER_COLUMN_VALUE_TYPE, "Indicates the value type of an attribute",
                                Ontology.VALUE_TYPE_NAMES, Ontology.NOMINAL), //
                        new ParameterTypeStringCategory(PARAMETER_COLUMN_ROLE, "Indicates the role of an attribute",
                                Attributes.KNOWN_ATTRIBUTE_TYPES, AbstractDataReader.AttributeColumn.REGULAR)));

        types.add(type);
//        types.add(new ParameterTypeBoolean(PARAMETER_ERROR_TOLERANT,
//                "Values which does not match to the specified value typed are considered as missings.", true, true));

        types.add(new ParameterTypeCategory(PARAMETER_DATAMANAGEMENT,
                "Determines, how the data is represented internally.", DataRowFactory.TYPE_NAMES,
                DataRowFactory.TYPE_DOUBLE_ARRAY));

        return types;
    }

    /**
     * This method might be overwritten by subclasses to avoid that the first row might be
     * misinterpreted as attribute names.
     */
    protected boolean isSupportingFirstRowAsNames() {
        return true;
    }
}
