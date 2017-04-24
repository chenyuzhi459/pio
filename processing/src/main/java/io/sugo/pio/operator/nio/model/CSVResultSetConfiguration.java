package io.sugo.pio.operator.nio.model;

import com.google.common.base.Strings;
import com.metamx.common.logger.Logger;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.nio.CSVExampleSource;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.tools.io.Encoding;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A class holding information about syntactical configuration for parsing CSV files
 */
public class CSVResultSetConfiguration implements DataResultSetFactory {

    private static final Logger logger = new Logger(CSVResultSetConfiguration.class);

    private String csvFile;

    private boolean skipComments = true;
    private boolean useQuotes = true;
    private boolean skipUTF8BOM = false;
    private boolean trimLines = false;
    private boolean hasHeaderRow = true;

    private String columnSeparators = ";";

    private char quoteCharacter = '"';
    private char escapeCharacter = '\\';
    private char decimalCharacter = '.';
    private String commentCharacters = "#";
    private int startingRow = 0;
    private int headerRow = 0;

    private Charset encoding = Charset.defaultCharset();

    private List<ParsingError> errors;

    /**
     * This will create a completely empty result set configuration
     */
    public CSVResultSetConfiguration() {
    }

    /**
     * This constructor reads all settings from the parameters of the given operator.
     */
    public CSVResultSetConfiguration(CSVExampleSource csvExampleSource) throws OperatorException {
        /*if (csvExampleSource.isFileSpecified()) {
            setCsvFile(csvExampleSource.getSelectedFile().getAbsolutePath());
        }*/
        setCsvFile(csvExampleSource.getParameterAsString(CSVExampleSource.PARAMETER_CSV_FILE));
        setSkipComments(csvExampleSource.getParameterAsBoolean(CSVExampleSource.PARAMETER_SKIP_COMMENTS));
        setUseQuotes(csvExampleSource.getParameterAsBoolean(CSVExampleSource.PARAMETER_USE_QUOTES));
        // setFirstRowAsAttributeNames(csvExampleSource.getParameterAsBoolean(CSVExampleSource.PARAMETER_USE_FIRST_ROW_AS_ATTRIBUTE_NAMES));
        setTrimLines(csvExampleSource.getParameterAsBoolean(CSVExampleSource.PARAMETER_TRIM_LINES));
        if (csvExampleSource.isParameterSet(CSVExampleSource.PARAMETER_COLUMN_SEPARATORS)) {
            setColumnSeparators(csvExampleSource.getParameterAsString(CSVExampleSource.PARAMETER_COLUMN_SEPARATORS));
        }
        if (csvExampleSource.isParameterSet(CSVExampleSource.PARAMETER_ESCAPE_CHARACTER)) {
            setEscapeCharacter(csvExampleSource.getParameterAsChar(CSVExampleSource.PARAMETER_ESCAPE_CHARACTER));
        }
        if (csvExampleSource.isParameterSet(CSVExampleSource.PARAMETER_COMMENT_CHARS)) {
            setCommentCharacters(csvExampleSource.getParameterAsString(CSVExampleSource.PARAMETER_COMMENT_CHARS));
        }
        if (csvExampleSource.isParameterSet(CSVExampleSource.PARAMETER_QUOTES_CHARACTER)) {
            setQuoteCharacter(csvExampleSource.getParameterAsChar(CSVExampleSource.PARAMETER_QUOTES_CHARACTER));
        }
        encoding = Encoding.getEncoding(csvExampleSource);
    }

    @Override
    public void setParameters(AbstractDataResultSetReader source) {
        source.setParameter(CSVExampleSource.PARAMETER_CSV_FILE, getCsvFile());
        source.setParameter(CSVExampleSource.PARAMETER_SKIP_COMMENTS, String.valueOf(isSkipComments()));
        source.setParameter(CSVExampleSource.PARAMETER_USE_QUOTES, String.valueOf(isUseQuotes()));
        // source.setParameter(CSVExampleSource.PARAMETER_USE_FIRST_ROW_AS_ATTRIBUTE_NAMES,
        // String.valueOf(isFirstRowAsAttributeNames()));
        source.setParameter(CSVExampleSource.PARAMETER_COLUMN_SEPARATORS, getColumnSeparators());
        source.setParameter(CSVExampleSource.PARAMETER_TRIM_LINES, String.valueOf(isTrimLines()));
        source.setParameter(CSVExampleSource.PARAMETER_QUOTES_CHARACTER, String.valueOf(getQuoteCharacter()));
        source.setParameter(CSVExampleSource.PARAMETER_ESCAPE_CHARACTER, String.valueOf(getEscapeCharacter()));
        source.setParameter(CSVExampleSource.PARAMETER_COMMENT_CHARS, getCommentCharacters());

        source.setParameter(Encoding.PARAMETER_ENCODING, encoding.name());
    }

    @Override
    public DataResultSet makeDataResultSet(Operator operator) throws OperatorException {
        if (!operator.getParameters().getExternalData().isEmpty()) {
            logger.info("CSVResultSetConfiguration found csv content in memory, and initiate the mode that read csv from memory.");
            operator.collectLog("Load data from memory.");
            return new CSVMemoryResultSet(this, operator);
        }

        if (Strings.isNullOrEmpty(csvFile)) {
            throw new OperatorException("pio.error.file_not_specfied", "");
        }

        logger.info("CSVResultSetConfiguration found csv file[%s], and initiate the mode that read csv from " +
                "file.", csvFile);
        operator.collectLog("Load data from csv file: " + csvFile);

        return new CSVResultSet(this, operator);
    }

	/*@Override
    public TableModel makePreviewTableModel(ProgressListener listener) throws OperatorException, ParseException {
		final DataResultSet resultSet = makeDataResultSet(null);
		DefaultPreview preview = null;
		try {
			this.errors = ((CSVResultSet) resultSet).getErrors();
			preview = new DefaultPreview(resultSet, listener);
		} finally {
			resultSet.close();
		}
		return preview;
	}*/

    public void setCsvFile(String csvFile) {
        this.csvFile = csvFile;
    }

    public String getCsvFile() {
        return csvFile;
    }

    public File getCsvFileAsFile() {
        return csvFile == null ? null : new File(csvFile);
    }

    public void setUseQuotes(boolean useQuotes) {
        this.useQuotes = useQuotes;
    }

    public boolean isUseQuotes() {
        return useQuotes;
    }

    public boolean hasHeaderRow() {
        return hasHeaderRow;
    }

    public void setHasHeaderRow(boolean hasHeaderRow) {
        this.hasHeaderRow = hasHeaderRow;
    }

    public void setSkipComments(boolean skipComments) {
        this.skipComments = skipComments;
    }

    public boolean isSkipComments() {
        return skipComments;
    }

    public void setColumnSeparators(String columnSeparators) {
        this.columnSeparators = columnSeparators;
    }

    public String getColumnSeparators() {
        return columnSeparators;
    }

    public void setCommentCharacters(String commentCharacters) {
        this.commentCharacters = commentCharacters;
    }

    public String getCommentCharacters() {
        return commentCharacters;
    }

    public void setEscapeCharacter(char escapeCharacter) {
        this.escapeCharacter = escapeCharacter;
    }

    public char getEscapeCharacter() {
        return escapeCharacter;
    }

    public void setQuoteCharacter(char quoteCharacter) {
        this.quoteCharacter = quoteCharacter;
    }

    public char getQuoteCharacter() {
        return quoteCharacter;
    }

    public void setDecimalCharacter(char decimalCharacter) {
        this.decimalCharacter = decimalCharacter;
    }

    public char getDecimalCharacter() {
        return decimalCharacter;
    }

    public void setTrimLines(boolean trimLines) {
        this.trimLines = trimLines;
    }

    public boolean isTrimLines() {
        return trimLines;
    }

    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }

    public Charset getEncoding() {
        return encoding;
    }

    public boolean isSkippingUTF8BOM() {
        return skipUTF8BOM;
    }

    public void setSkipUTF8BOM(boolean skipUTF8BOM) {
        this.skipUTF8BOM = skipUTF8BOM;
    }

    public int getStartingRow() {
        return startingRow;
    }

    public void setStartingRow(int startingRow) {
        this.startingRow = startingRow;
    }

    public int getHeaderRow() {
        return headerRow;
    }

    public void setHeaderRow(int headerRow) {
        this.headerRow = headerRow;
    }

    @Override
    public String getResourceName() {
        return getCsvFile();
    }

    @Override
    public ExampleSetMetaData makeMetaData() {
        return new ExampleSetMetaData();
    }

    public List<ParsingError> getErrors() {
        return errors;
    }

    @Override
    public void close() {
    }

    /**
     * @return a map containing all fieldNames and their values
     */
    public Map<String, String> getParameterMap() {
        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("csvFile", getCsvFile());
        parameterMap.put("useQuotes", String.valueOf(isUseQuotes()));
        parameterMap.put("hasHeaderRow", String.valueOf(hasHeaderRow()));
        parameterMap.put("headerRow", String.valueOf(getHeaderRow()));
        parameterMap.put("decimalCharacter", String.valueOf(getDecimalCharacter()));
        parameterMap.put("startingRow", String.valueOf(getStartingRow()));
        parameterMap.put("skipComments", String.valueOf(isSkipComments()));
        parameterMap.put("columnSeparators", getColumnSeparators());
        parameterMap.put("commentCharacters", getCommentCharacters());
        parameterMap.put("escapeCharacter", String.valueOf(getEscapeCharacter()));
        parameterMap.put("quoteCharacter", String.valueOf(getQuoteCharacter()));
        parameterMap.put("trimLines", String.valueOf(isTrimLines()));
        parameterMap.put("encoding", String.valueOf(getEncoding()));
        parameterMap.put("skipUTF8BOM", String.valueOf(isSkippingUTF8BOM()));
        return parameterMap;
    }
}
