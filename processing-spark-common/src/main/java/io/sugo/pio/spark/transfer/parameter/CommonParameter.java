package io.sugo.pio.spark.transfer.parameter;

/**
 */
public class CommonParameter extends SparkParameter {
    private String outputDir;
    private String inputDir;
    private FileFormat inputFormat;
    private String fieldSeparator;
    private String nullString;
    private String[] columnNames;
    private Boolean[] isNominal;
    private Boolean[] isFeature;
    private int labelIndex;
    private String[] negativeValues;
    private String[] positiveValues;

    public CommonParameter() {
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public String getInputDir() {
        return inputDir;
    }

    public void setInputDir(String inputDir) {
        this.inputDir = inputDir;
    }

    public FileFormat getInputFormat() {
        return inputFormat;
    }

    public void setInputFormat(FileFormat inputFormat) {
        this.inputFormat = inputFormat;
    }

    public String getFieldSeparator() {
        return fieldSeparator;
    }

    public void setFieldSeparator(String fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
    }

    public String getNullString() {
        return nullString;
    }

    public void setNullString(String nullString) {
        this.nullString = nullString;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public Boolean[] getIsNominal() {
        return isNominal;
    }

    public void setIsNominal(Boolean[] isNominal) {
        this.isNominal = isNominal;
    }

    public Boolean[] getIsFeature() {
        return isFeature;
    }

    public void setIsFeature(Boolean[] isFeature) {
        this.isFeature = isFeature;
    }

    public int getLabelIndex() {
        return labelIndex;
    }

    public void setLabelIndex(int labelIndex) {
        this.labelIndex = labelIndex;
    }

    public String[] getNegativeValues() {
        return negativeValues;
    }

    public void setNegativeValues(String[] negativeValues) {
        this.negativeValues = negativeValues;
    }

    public String[] getPositiveValues() {
        return positiveValues;
    }

    public void setPositiveValues(String[] positiveValues) {
        this.positiveValues = positiveValues;
    }

    public enum FileFormat {
        TEXTFILE,
        PARQUET;

        private FileFormat() {
        }
    }
}
