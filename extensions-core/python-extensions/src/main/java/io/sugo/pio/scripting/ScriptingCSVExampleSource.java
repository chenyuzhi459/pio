package io.sugo.pio.scripting;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.nio.CSVExampleSource;
import io.sugo.pio.operator.nio.model.ColumnMetaData;
import io.sugo.pio.operator.nio.model.DataResultSet;
import io.sugo.pio.operator.nio.model.DataResultSetTranslationConfiguration;
import io.sugo.pio.operator.nio.model.DataResultSetTranslator;
import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.StrictDecimalFormat;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 */
public class ScriptingCSVExampleSource extends CSVExampleSource {
    private DecimalFormat specialDecimalFormat;
    private List<String[]> metadata;

    public ScriptingCSVExampleSource() {
        super();
    }

    protected NumberFormat getNumberFormat() throws OperatorException {
        return specialDecimalFormat == null ? StrictDecimalFormat.getInstance(this, true) : specialDecimalFormat;
    }

    public void setNumberFormat(DecimalFormat decimalFormat) {
        specialDecimalFormat = decimalFormat;
    }

    protected ExampleSet transformDataResultSet(DataResultSet dataResultSet)
            throws OperatorException {
        DataResultSetTranslationConfiguration configuration = new DataResultSetTranslationConfiguration(this);

        DataResultSetTranslator translator = new DataResultSetTranslator(this);
        NumberFormat numberFormat = getNumberFormat();
        if (numberFormat != null) {
            configuration.setNumberFormat(numberFormat);
        }
        ColumnMetaData[] columnMetaData = prepareMetaData(dataResultSet, this.metadata);
        if (columnMetaData != null) {
            configuration.setColumnMetaData(columnMetaData);
        } else if (!configuration.isComplete()) {
            configuration.reconfigure(dataResultSet);
            translator.guessValueTypes(configuration, dataResultSet, 3);
        }
        return translator.read(dataResultSet, configuration, false);
    }

    public void setMetadata(List<String[]> metadata) {
        this.metadata = metadata;
    }

    public void readMetadataFromFile(File metadataFile) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            this.metadata = mapper.readValue(metadataFile, new TypeReference<List<String[]>>() {
            });
        } catch (IOException e) {
            this.metadata = null;
        }
    }

    protected ColumnMetaData[] prepareMetaData(DataResultSet dataResultSet, List<String[]> metadata) {
        if ((dataResultSet == null) || (metadata == null)) {
            return null;
        }
        int numberOfColumns = dataResultSet.getNumberOfColumns();
        ColumnMetaData[] columnMetaData = new ColumnMetaData[numberOfColumns];
        String[] originalColumnNames = dataResultSet.getColumnNames();
        try {
            for (int i = 0; i < numberOfColumns; i++) {
                String[] typeRolePair = (String[]) metadata.get(i);
                String attributeRole = "attribute";
                int valueType = 7;
                if (typeRolePair != null) {
                    String valueTypeName = typeRolePair[0].trim();
                    valueType = Ontology.ATTRIBUTE_VALUE_TYPE.mapName(valueTypeName);
                    if (valueType < 0) {
                        valueType = 7;
                    }
                    attributeRole = typeRolePair[1].trim();
                }
                columnMetaData[i] = new ColumnMetaData(originalColumnNames[i], originalColumnNames[i], valueType, attributeRole, true);
            }
            return columnMetaData;
        } catch (IndexOutOfBoundsException e) {
        }
        return null;
    }

}
