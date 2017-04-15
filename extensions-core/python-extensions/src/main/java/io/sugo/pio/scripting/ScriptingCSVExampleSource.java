package io.sugo.pio.scripting;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.nio.CSVExampleSource;
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

}
