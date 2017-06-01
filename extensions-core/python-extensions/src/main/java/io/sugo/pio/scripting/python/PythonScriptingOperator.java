package io.sugo.pio.scripting.python;

import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.operator.UserError;
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
        return I18N.getMessage("pio.PythonScriptingOperator.name");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.script;
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.PythonScriptingOperator.description");
    }

    @Override
    protected ScriptRunner getScriptRunner() throws UndefinedParameterError {
        String script = getParameter("script");
        PythonScriptRunner runner = new PythonScriptRunner(script, this);
        runner.registerLogger(Logger.getLogger(PythonScriptingOperator.class.getName()));
        return runner;
    }

    /*protected void showSetupProblems() {
        PythonSetupTester tester = PythonSetupTester.INSTANCE;
        if (tester.wasPythonFound()) {
            if (this.configurationLink != null) {
                this.configurationLink.setHidden(true);
            }
            if (!tester.wasPandasFound()) {
                QuickFix fix = new AbstractQuickFix(1, true, "python_scripting.pandas", new Object[0]) {
                    public void apply() {
                        try {
                            RMUrlHandler.openInBrowser(new URI(
                                    I18N.getGUIMessage("gui.label.python_scripting.download_anaconda.url", new Object[0])));
                        } catch (Exception ex) {
                            SwingTools.showSimpleErrorMessage("cannot_open_browser", ex, new Object[0]);
                        }
                    }
                };
                addError(new SimpleProcessSetupError(ProcessSetupError.Severity.ERROR, getPortOwner(), Collections.singletonList(fix), "python_scripting.pandas", new Object[0]));
            } else if (!tester.wasPandasVersionSufficient()) {
                addError(new SimpleProcessSetupError(ProcessSetupError.Severity.ERROR, getPortOwner(), "python_scripting.pandas_version", new Object[]{"0.12.0"}));
            }
            if (!tester.wasCpickleFound()) {
                addError(new SimpleProcessSetupError(ProcessSetupError.Severity.WARNING, getPortOwner(), "python_scripting.cpickle", new Object[0]));
            }
        } else {
            QuickFix fix = new AbstractQuickFix(1, true, "python_scripting.configure", new Object[0]) {
                public void apply() {
                    new SettingsDialog("python_scripting").setVisible(true);
                }
            };
            SimpleProcessSetupError error = new SimpleProcessSetupError(ProcessSetupError.Severity.ERROR, getPortOwner(), Collections.singletonList(fix), "python_scripting.not_found", new Object[]{ParameterService.getParameterValue("rapidminer.python_scripting.path")});

            addError(error);
            if (this.configurationLink != null) {
                this.configurationLink.setHidden(false);
            }
        }
    }*/

    public void doWork() throws OperatorException {
//        if (PythonSetupTester.INSTANCE.isPythonInstalled()) {
            super.doWork();
//        } else {
//            throw new UserError(this, "python_scripting.setup_test.failure");
//        }
    }

    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();

        ParameterTypeText type = new ParameterTypeText("script", I18N.getMessage("pio.PythonScriptingOperator.script"),
                TextType.PYTHON, false);
        type.setExpert(false);
        String templateText = "import pandas\n\n# rm_main is a mandatory function, \n# the number of arguments has to be the number of input ports (can be none)\ndef rm_main(data):\n    print('Hello, world!')\n    # output can be found in Log View\n    print(type(data))\n\n    #your code goes here\n\n    #for example:\n    data2 = pandas.DataFrame([3,5,77,8])\n\n    # connect 2 output ports to see the results\n    return data, data2";
        type.setTemplateText(templateText);
        types.add(type);

        return types;
    }
}
