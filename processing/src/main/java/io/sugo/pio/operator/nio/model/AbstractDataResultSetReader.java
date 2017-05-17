package io.sugo.pio.operator.nio.model;

import com.google.common.base.Strings;
import com.metamx.common.logger.Logger;
import io.sugo.pio.constant.PortConstant;
import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.Annotations;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.io.AbstractExampleSource;
import io.sugo.pio.operator.nio.file.FileInputPortHandler;
import io.sugo.pio.operator.nio.file.FileObject;
import io.sugo.pio.parameter.*;
import io.sugo.pio.parameter.conditions.BooleanParameterCondition;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.ports.metadata.MetaData;
import io.sugo.pio.ports.metadata.SimplePrecondition;
import io.sugo.pio.tools.Ontology;

import java.io.File;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * This class uses DataResultSets to load data from file and then delivers the data as an example
 * set.
 */
public abstract class AbstractDataResultSetReader extends AbstractExampleSource {

    private static final Logger logger = new Logger(AbstractDataResultSetReader.class);

    /**
     * Pseudo-annotation to be used for attribute names.
     */
    public static final String ANNOTATION_NAME = "Name";

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

    public static final String PARAMETER_DATE_FORMAT = "date_format";
    public static final String PARAMETER_TIME_ZONE = "time_zone";
    public static final String PARAMETER_LOCALE = "locale";

    public static final String PARAMETER_ATTRIBUTES = "attributes";

    /**
     * The parameter name for &quot;Determines, how the data is represented internally.&quot;
     */
    public static final String PARAMETER_DATAMANAGEMENT = "datamanagement";
    public static final String PARAMETER_FIRST_ROW_AS_NAMES = "first_row_as_names";
    public static final String PARAMETER_ANNOTATIONS = "annotations";

    public static final String PARAMETER_ERROR_TOLERANT = "read_not_matching_values_as_missings";

//    private InputPort fileInputPort = getInputPorts().createPort("file");

//    public InputPort getFileInputPort() {
//        return fileInputPort;
//    }

    private InputPort fileInputPort = getInputPorts().createPort(PortConstant.FILE, PortConstant.FILE_DESC);
    private FileInputPortHandler filePortHandler = new FileInputPortHandler(this, fileInputPort, this.getFileParameterName());

    public AbstractDataResultSetReader() {
        super();
        fileInputPort.addPrecondition(new SimplePrecondition(fileInputPort, new MetaData(FileObject.class)) {

            @Override
            protected boolean isMandatory() {
                return false;
            }
        });
    }

    @Override
    public ExampleSet createExampleSet() throws OperatorException {
        logger.info("AbstractDataResultSetReader begin to load data result set...");
        collectLog("Begin to load data result set...");

        String metaData = getParameterAsString(PARAMETER_META_DATA);
        if (Strings.isNullOrEmpty(metaData)) {
            throw new OperatorException("pio.error.file_metadata_not_set");
        }

        // loading data result set
        final ExampleSet exampleSet;
        try (DataResultSetFactory dataResultSetFactory = getDataResultSetFactory();
             DataResultSet dataResultSet = dataResultSetFactory.makeDataResultSet(this)) {
            exampleSet = transformDataResultSet(dataResultSet);
        }
        if (fileInputPort.isConnected()) {
            IOObject fileObject = fileInputPort.getDataOrNull(IOObject.class);
            if (fileObject != null) {
                String sourceAnnotation = fileObject.getAnnotations().getAnnotation(Annotations.KEY_SOURCE);
                if (sourceAnnotation != null) {
                    exampleSet.getAnnotations().setAnnotation(Annotations.KEY_SOURCE, sourceAnnotation);
                }
            }
        }

        logger.info("AbstractDataResultSetReader load data of size[%d].", exampleSet.size());
        collectLog("Load data of size: " + exampleSet.size());

        return exampleSet;
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
//        configuration.setParameters(this);
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
    public MetaData getGeneratedMetaData() throws OperatorException {
//        this.getParameters().setParameter(PARAMETER_META_DATA, "0:NO.true.integer.id;1:category.true.polynominal.label;2:attribute_1.true.real.attribute;3:attribute_2.true.real.attribute;4:attribute_3.true.real.attribute;5:attribute_4.true.real.attribute;6:attribute_5.true.real.attribute;7:attribute_6.true.real.attribute;8:attribute_7.true.real.attribute;9:attribute_8.true.real.attribute;10:attribute_9.true.real.attribute;11:attribute_10.true.real.attribute");
//        this.getParameters().setParameter("csv_file", "F:/lr.csv");
        ExampleSetMetaData result = new ExampleSetMetaData();
        ColumnMetaData[] columnMetaDatas = DataResultSetTranslationConfiguration.readColumnMetaData(this);
        if (columnMetaDatas.length > 0) {
            for (ColumnMetaData columnMetaData : columnMetaDatas) {
                if (columnMetaData.isSelected()) {
                    result.addAttribute(columnMetaData.getAttributeMetaData());
                }
            }
        }

        return result;
    }

    /*@Override
    public MetaData getGeneratedMetaData() throws OperatorException {
        try (DataResultSetFactory dataResultSetFactory = getDataResultSetFactory()) {
            ExampleSetMetaData result = dataResultSetFactory.makeMetaData();
            DataResultSetTranslationConfiguration configuration = new DataResultSetTranslationConfiguration(this);
            configuration.addColumnMetaData(result);
            return result;
        }
    }*/

    public InputPort getFileInputPort() {
        return fileInputPort;
    }

    protected abstract DataResultSetFactory getDataResultSetFactory();

    /**
     * Returns the configured number format or null if a default number format should be used.
     */
    protected abstract NumberFormat getNumberFormat() throws OperatorException;

    /**
     * Returns the name of the {@link ParameterTypeFile} to be added through which the user can
     * specify the file name.
     */
    protected abstract String getFileParameterName();

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

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new LinkedList<ParameterType>();

        if (isSupportingFirstRowAsNames()) {
            types.add(new ParameterTypeBoolean(
                    PARAMETER_FIRST_ROW_AS_NAMES,
                    "Indicates if the first row should be used for the attribute names. If activated no annotations can be used.",
                    true, false, true));
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

//        type = new ParameterTypeDateFormat(PARAMETER_DATE_FORMAT,
//                "The parse format of the date values, for example \"yyyy/MM/dd\".", "yyyy/MM/dd");
//        types.add(type);

//        type = new ParameterTypeCategory(PARAMETER_TIME_ZONE,
//                "The time zone used for the date objects if not specified in the date string itself.",
//                Tools.getAllTimeZones(), Tools.getPreferredTimeZoneIndex());
//        types.add(type);

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
                        new ParameterTypeStringCategory(PARAMETER_COLUMN_VALUE_TYPE, "Indicates the value type of an attribute",
                                Ontology.VALUE_TYPE_NAMES_VALUE, Ontology.VALUE_TYPE_NAMES, "attribute_value", true), //
                        new ParameterTypeStringCategory(PARAMETER_COLUMN_ROLE, "Indicates the role of an attribute",
                                Attributes.KNOWN_ATTRIBUTE_TYPES_VALUE, Attributes.KNOWN_ATTRIBUTE_TYPES, Attributes.ATTRIBUTE_NAME, true)));

        types.add(type);
//        types.add(new ParameterTypeBoolean(PARAMETER_ERROR_TOLERANT,
//                "Values which does not match to the specified value typed are considered as missings.", true, true));

//        types.add(new ParameterTypeCategory(PARAMETER_DATAMANAGEMENT,
//                "Determines, how the data is represented internally.", DataRowFactory.TYPE_NAMES,
//                DataRowFactory.TYPE_DOUBLE_ARRAY));

        return types;
    }

    /**
     * This method might be overwritten by subclasses to avoid that the first row might be
     * misinterpreted as attribute names.
     */
    protected boolean isSupportingFirstRowAsNames() {
        return true;
    }

    /**
     * Returns either the selected file referenced by the value of the parameter with the name
     * {@link #getFileParameterName()} or the file delivered at {@link #fileInputPort}. Which of
     * these options is chosen is determined by the parameter {@link #}.
     */
    public File getSelectedFile() throws OperatorException {
        return filePortHandler.getSelectedFile();
    }

    /**
     * Same as {@link #getSelectedFile()}, but returns true if file is specified (in the respective
     * way).
     */
    public boolean isFileSpecified() {
        return filePortHandler.isFileSpecified();
    }
}
