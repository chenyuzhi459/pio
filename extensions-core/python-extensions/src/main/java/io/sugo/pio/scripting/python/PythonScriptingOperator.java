package io.sugo.pio.scripting.python;

import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.scripting.AbstractScriptingLanguageOperator;
import io.sugo.pio.scripting.ScriptRunner;

/**
 */
public class PythonScriptingOperator extends AbstractScriptingLanguageOperator {
    @Override
    public String getDefaultFullName() {
        return PythonScriptingOperator.class.getName();
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.dataSource;
    }

    @Override
    public String getDescription() {
        return PythonScriptingOperator.class.getName();
    }

    @Override
    protected ScriptRunner getScriptRunner() throws UndefinedParameterError {
        String script = getParameter("script");
        PythonScriptRunner runner = new PythonScriptRunner(script, this);
        return runner;
    }

    public void doWork() throws OperatorException {
//        if(PythonSetupTester.INSTANCE.isPythonInstalled()) {
            super.doWork();
//        } else {
//            throw new UserError(this, "python_scripting.setup_test.failure");
//        }
    }
}
