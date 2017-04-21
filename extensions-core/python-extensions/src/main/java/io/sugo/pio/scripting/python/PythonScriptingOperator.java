package io.sugo.pio.scripting.python;

import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeText;
import io.sugo.pio.parameter.TextType;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.scripting.AbstractScriptingLanguageOperator;
import io.sugo.pio.scripting.ScriptRunner;

import java.util.List;
import java.util.logging.Logger;

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
        runner.registerLogger(Logger.getLogger(PythonScriptingOperator.class.getName()));
        return runner;
    }

    public void doWork() throws OperatorException {
//        if(PythonSetupTester.INSTANCE.isPythonInstalled()) {
        super.doWork();
//        } else {
//            throw new UserError(this, "python_scripting.setup_test.failure");
//        }
    }

    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();

        ParameterTypeText type = new ParameterTypeText("script", "The python script to execute.", TextType.PYTHON, false);
        type.setExpert(false);
        String templateText = "import pandas\n\n# rm_main is a mandatory function, \n# the number of arguments has to be the number of input ports (can be none)\ndef rm_main(data):\n    print('Hello, world!')\n    # output can be found in Log View\n    print(type(data))\n\n    #your code goes here\n\n    #for example:\n    data2 = pandas.DataFrame([3,5,77,8])\n\n    # connect 2 output ports to see the results\n    return data, data2";
        type.setTemplateText(templateText);
        types.add(type);

        return types;
    }
}
