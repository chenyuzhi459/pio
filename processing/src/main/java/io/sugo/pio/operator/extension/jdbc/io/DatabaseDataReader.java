package io.sugo.pio.operator.extension.jdbc.io;


import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.table.AttributeFactory;
import io.sugo.pio.example.table.DataRow;
import io.sugo.pio.example.table.DataRowFactory;
import io.sugo.pio.example.util.ExampleSetBuilder;
import io.sugo.pio.example.util.ExampleSets;
import io.sugo.pio.operator.*;
import io.sugo.pio.operator.extension.jdbc.tools.jdbc.DatabaseHandler;
import io.sugo.pio.operator.extension.jdbc.tools.jdbc.StatementCreator;
import io.sugo.pio.operator.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import io.sugo.pio.operator.extension.jdbc.tools.jdbc.connection.ConnectionProvider;
import io.sugo.pio.operator.io.AbstractExampleSource;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.ports.metadata.MetaData;
import io.sugo.pio.tools.Ontology;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseDataReader extends AbstractExampleSource implements ConnectionProvider {
    public static final String PARAMETER_QUERY = "query";
    public static final int DATA_MANAGEMENT = 0;

    private DatabaseHandler databaseHandler;

    public ExampleSet read() throws OperatorException {
        ExampleSet var2;
        try {
            ExampleSet result = super.read();
            var2 = result;
        } finally {
            if (this.databaseHandler != null && this.databaseHandler.getConnection() != null) {
                try {
                    this.databaseHandler.getConnection().close();
                } catch (SQLException var9) {
                    this.getLogger().log(Level.WARNING, "Error closing database connection: " + var9, var9);
                }
            }

        }

        return var2;
    }

    protected ResultSet getResultSet() throws OperatorException {
        try {
            this.databaseHandler = DatabaseHandler.getConnectedDatabaseHandler(this);
            String sqle = this.getQuery(this.databaseHandler.getStatementCreator());
            if (sqle == null) {
                throw new UserError(this, 202, new Object[]{"query", "query_file", "table_name"});
            } else {
                return this.databaseHandler.executeStatement(sqle, true, this, this.getLogger());
            }
        } catch (SQLException var2) {
            if (this.databaseHandler != null && this.databaseHandler.isCancelled()) {
                throw new ProcessStoppedException(this);
            } else {
                throw new UserError(this, var2, 304, new Object[]{var2.getMessage()});
            }
        }
    }

    public ExampleSet createExampleSet() throws OperatorException {
        ResultSet resultSet = this.getResultSet();

        ExampleSetBuilder builder;
        try {
            List e = getAttributes(resultSet);
//            builder = createExampleTable(resultSet, e, this.getParameterAsInt("datamanagement"), this.getLogger(), this);
            builder = createExampleTable(resultSet, e, DATA_MANAGEMENT, this.getLogger(), this);
        } catch (SQLException var11) {
            throw new UserError(this, var11, 304, new Object[]{var11.getMessage()});
        } finally {
            try {
                resultSet.close();
            } catch (SQLException var10) {
                this.getLogger().log(Level.WARNING, "DB error closing result set: " + var10, var10);
            }

        }

        return builder.build();
    }

    public MetaData getGeneratedMetaData() throws OperatorException {
        ExampleSetMetaData metaData = new ExampleSetMetaData();
        try {
            this.databaseHandler = DatabaseHandler.getConnectedDatabaseHandler(this);
            String query1 = this.getQuery(this.databaseHandler.getStatementCreator());
            PreparedStatement prepared1 = this.databaseHandler.getConnection().prepareStatement(query1);
            List attributes = getAttributes(prepared1.getMetaData());
            Iterator var6 = attributes.iterator();

            while (var6.hasNext()) {
                Attribute att = (Attribute) var6.next();
                metaData.addAttribute(new AttributeMetaData(att));
            }

            prepared1.close();
        } catch (SQLException var16) {
//                LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "io.sugo.pio.operator.io.DatabaseDataReader.fetching_meta_data_error", new Object[]{var16}), var16);
        } finally {
            try {
                if (this.databaseHandler != null && this.databaseHandler.getConnection() != null) {
                    this.databaseHandler.disconnect();
                }
            } catch (SQLException var15) {
                this.getLogger().log(Level.WARNING, "DB error closing connection: " + var15, var15);
            }

        }

        return metaData;
    }

    public static ExampleSetBuilder createExampleTable(ResultSet resultSet, List<Attribute> attributes, int dataManagementType, Logger logger, Operator op) throws SQLException, OperatorException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        Attribute[] attributeArray = (Attribute[]) attributes.toArray(new Attribute[attributes.size()]);
        ExampleSetBuilder builder = ExampleSets.from(attributes);
        DataRowFactory factory = new DataRowFactory(dataManagementType, '.');
        int counter = 0;

        while (resultSet.next()) {
            DataRow dataRow = factory.create(attributeArray.length);

            for (int i = 1; i <= metaData.getColumnCount(); ++i) {
                Attribute attribute = attributeArray[i - 1];
                int valueType = attribute.getValueType();
                double value;
                if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(valueType, 9)) {
                    Timestamp var30 = resultSet.getTimestamp(i);
                    if (resultSet.wasNull()) {
                        value = 0.0D / 0.0;
                    } else {
                        value = (double) var30.getTime();
                    }
                } else if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(valueType, 2)) {
                    value = resultSet.getDouble(i);
                    if (resultSet.wasNull()) {
                        value = 0.0D / 0.0;
                    }
                } else if (!Ontology.ATTRIBUTE_VALUE_TYPE.isA(valueType, 1)) {
                    if (logger != null) {
                        logger.warning("Unknown column type: " + attribute);
                    }

                    value = 0.0D / 0.0;
                } else {
                    String valueString;
                    if (metaData.getColumnType(i) == 2005) {
                        Clob clob = resultSet.getClob(i);
                        if (clob != null) {
                            BufferedReader in = null;

                            try {
                                in = new BufferedReader(clob.getCharacterStream());
                                String e = null;

                                try {
                                    StringBuffer e1 = new StringBuffer();

                                    while ((e = in.readLine()) != null) {
                                        e1.append(e + "\n");
                                    }

                                    valueString = e1.toString();
                                } catch (IOException var28) {
                                    throw new OperatorException("Database error occurred: " + var28, var28);
                                }
                            } finally {
                                try {
                                    in.close();
                                } catch (IOException var27) {
                                    ;
                                }

                            }
                        } else {
                            valueString = null;
                        }
                    } else {
                        valueString = resultSet.getString(i);
                    }

                    if (!resultSet.wasNull() && valueString != null) {
                        value = (double) attribute.getMapping().mapString(valueString);
                    } else {
                        value = 0.0D / 0.0;
                    }
                }

                dataRow.set(attribute, value);
            }

            builder.addDataRow(dataRow);
//            if(op != null) {
//                ++counter;
//                if(counter % 100 == 0) {
//                    op.checkForStop();
//                }
//            }
        }

        return builder;
    }

    public static List<Attribute> getAttributes(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        return getAttributes(metaData);
    }

    private static List<Attribute> getAttributes(ResultSetMetaData metaData) throws SQLException {
        LinkedList result = new LinkedList();
        if (metaData != null) {
            HashMap duplicateNameMap = new HashMap();

            for (int columnIndex = 1; columnIndex <= metaData.getColumnCount(); ++columnIndex) {
                String dbColumnName = metaData.getColumnLabel(columnIndex);
                String columnName = dbColumnName;
                Integer duplicateCount = (Integer) duplicateNameMap.get(dbColumnName);
                boolean isUnique = duplicateCount == null;
                if (isUnique) {
                    duplicateNameMap.put(dbColumnName, new Integer(1));
                } else {
                    while (!isUnique) {
                        duplicateCount = new Integer(duplicateCount.intValue() + 1);
                        columnName = dbColumnName + "_" + (duplicateCount.intValue() - 1);
                        isUnique = duplicateNameMap.get(columnName) == null;
                    }

                    duplicateNameMap.put(dbColumnName, duplicateCount);
                }

                int attributeType = DatabaseHandler.getRapidMinerTypeIndex(metaData.getColumnType(columnIndex));
                Attribute attribute = AttributeFactory.createAttribute(columnName, attributeType);
                attribute.getAnnotations().setAnnotation("sql_type", metaData.getColumnTypeName(columnIndex));
                result.add(attribute);
            }
        }

        return result;
    }

    private String getQuery(StatementCreator sc) throws OperatorException {
        String query = this.getParameterAsString(PARAMETER_QUERY);
        if (query != null) {
            query = query.trim();
        }

        if (query == null) {
            throw new UserError(this, 202, new Object[]{PARAMETER_QUERY, "query_file"});
        } else {
            return query;
        }
    }

    public ConnectionEntry getConnectionEntry() {
        return DatabaseHandler.getConnectionEntry(this);
    }

    protected void addAnnotations(ExampleSet result) {
        try {
            if (this.databaseHandler != null) {
                result.getAnnotations().setAnnotation("Source", this.getQuery(this.databaseHandler.getStatementCreator()));
            }
        } catch (OperatorException var3) {
            ;
        }

    }

    protected boolean isMetaDataCacheable() {
        return true;
    }

    @Override
    public String getFullName() {
        return "DatabaseDataReader";
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.source;
    }

    @Override
    public String getDescription() {
        return "DatabaseDataReader";
    }

    public List<ParameterType> getParameterTypes() {
        List list = super.getParameterTypes();
        list.addAll(DatabaseHandler.getConnectionParameterTypes(this));
        list.addAll(DatabaseHandler.getQueryParameterTypes(this, false));
//        list.addAll(DatabaseHandler.getStatementPreparationParamterTypes(this));
//        list.add(new ParameterTypeCategory("datamanagement", "Determines, how the data is represented internally.", DataRowFactory.TYPE_NAMES, 0));
        return list;
    }
}