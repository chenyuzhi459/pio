package io.sugo.pio.operator.extension.jdbc.tools.jdbc;


import java.sql.ResultSet;
import java.sql.SQLException;

public class DataTypeSyntaxInformation {
    private static final String NETEZZA_NAME = "Netezza";
    private final String literalPrefix;
    private final String literalSuffix;
    private final int dataType;
    private final String typeName;
    private String createParams;
    private long precision;

    public DataTypeSyntaxInformation(ResultSet typesResult, String databaseName) throws SQLException {
        this.typeName = typesResult.getString("TYPE_NAME");
        this.dataType = typesResult.getInt("DATA_TYPE");
        this.literalPrefix = typesResult.getString("LITERAL_PREFIX");
        this.literalSuffix = typesResult.getString("LITERAL_SUFFIX");
        if (databaseName.contains("Netezza")) {
            try {
                this.precision = typesResult.getLong("PRECISION1");
            } catch (SQLException var4) {
                this.precision = typesResult.getLong("PRECISION");
            }
        } else {
            this.precision = typesResult.getLong("PRECISION");
        }

        this.createParams = typesResult.getString("CREATE_PARAMS");
    }

    public DataTypeSyntaxInformation(String literalPrefix, String literalSuffix, int dataType, String typeName, String createParams, long precision) {
        this.literalPrefix = literalPrefix;
        this.literalSuffix = literalSuffix;
        this.dataType = dataType;
        this.typeName = typeName;
        this.createParams = createParams;
        this.precision = precision;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public int getDataType() {
        return this.dataType;
    }

    public String toString() {
        return this.getTypeName() + " (prec=" + this.precision + "; params=" + this.createParams + ")";
    }

    public long getPrecision() {
        return this.precision;
    }

    public String getLiteralPrefix() {
        return this.literalPrefix;
    }

    public String getLiteralSuffix() {
        return this.literalSuffix;
    }
}