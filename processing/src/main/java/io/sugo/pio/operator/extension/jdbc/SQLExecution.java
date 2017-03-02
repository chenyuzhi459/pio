package io.sugo.pio.operator.extension.jdbc;


import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.operator.extension.jdbc.tools.jdbc.DatabaseHandler;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeText;
import io.sugo.pio.parameter.TextType;
import io.sugo.pio.ports.DummyPortPairExtender;
import io.sugo.pio.ports.PortPairExtender;

import java.sql.SQLException;
import java.util.List;

public class SQLExecution extends Operator {
    public static final String PARAMETER_QUERY = "query";
    private PortPairExtender dummyPorts = new DummyPortPairExtender("through", this.getInputPorts(), this.getOutputPorts());

    public SQLExecution() {
        this.dummyPorts.start();
        this.getTransformer().addRule(this.dummyPorts.makePassThroughRule());
    }

    @Override
    public String getDefaultFullName() {
        return "SQLExecution";
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.source;
    }

    @Override
    public String getDescription() {
        return "SQLExecution";
    }

    public void doWork() throws OperatorException {
        try {
            DatabaseHandler sqle = DatabaseHandler.getConnectedDatabaseHandler(this);
            String query = this.getQuery();
            sqle.executeStatement(query, true, this, this.getLogger());
            sqle.disconnect();
        } catch (SQLException var3) {
            throw new UserError(this, var3, "pio.error.database_error", new Object[]{var3.getMessage()});
        }

        this.dummyPorts.passDataThrough();
    }

    private String getQuery() throws OperatorException {
        String query = this.getParameterAsString(PARAMETER_QUERY);
        if (query != null) {
            query = query.trim();
        }

        if (query == null) {
            throw new UserError(this, "pio.error.parameter_must_set",
                    new Object[]{PARAMETER_QUERY, "query_file"});
        } else {
            return query;
        }
    }

    public List<ParameterType> getParameterTypes() {
        List types = super.getParameterTypes();
        types.addAll(DatabaseHandler.getConnectionParameterTypes(this));
        ParameterTypeText type = new ParameterTypeText(PARAMETER_QUERY, "SQL query. If not set, the query is read from the file specified by \'query_file\'.", TextType.SQL, false);
        types.add(type);
//        types.add(new ParameterTypeFile("query_file", "File containing the query. Only evaluated if \'query\' is not set.", (String) null, true));
//        types.addAll(DatabaseHandler.getStatementPreparationParamterTypes(this));
        return types;
    }
}