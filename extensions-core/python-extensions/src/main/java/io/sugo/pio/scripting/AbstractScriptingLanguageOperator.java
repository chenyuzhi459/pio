package io.sugo.pio.scripting;

import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.scripting.metadata.MetaDataCachingRule;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CancellationException;

/**
 */
public abstract class AbstractScriptingLanguageOperator extends Operator {
//    private final InputPortExtender inExtender = new InputPortExtender("input", this.getInputPorts());
//    private final OutputPortExtender outExtender = new OutputPortExtender("output", this.getOutputPorts());
    private final MetaDataCachingRule cachingRule = new MetaDataCachingRule(this);

    protected abstract ScriptRunner getScriptRunner() throws UndefinedParameterError;

    @Override
    public void doWork() throws OperatorException {
//        ScriptRunner scriptRunner = getScriptRunner();
//        List inputs = checkInputTypes(scriptRunner);
//        int numberOfOutputPorts = outExtender.getManagedPorts().size() - 1;
//
//        try {
//            List e = scriptRunner.run(inputs, numberOfOutputPorts);
//            outExtender.deliver(e);
//            cachingRule.setOperatorWorked();
//        } catch (CancellationException e) {
//            this.checkForStop();
//            throw new OperatorException("python_scripting.execution_interruption", e, new Object[0]);
//        } catch (IOException e) {
//            throw new OperatorException("python_scripting.execution_failed", e, new Object[0]);
//        }
    }

//    private List<IOObject> checkInputTypes(ScriptRunner scriptRunner) throws UserError {
//        List supportedTypes = scriptRunner.getSupportedTypes();
//        List inputs = inExtender.getData(IOObject.class, false);
//        int index = 0;
//
//        for(Iterator var5 = inputs.iterator(); var5.hasNext(); ++index) {
//            IOObject input = (IOObject)var5.next();
//            boolean contained = false;
//            Iterator iterator = supportedTypes.iterator();
//
//            while(iterator.hasNext()) {
//                Class type = (Class)iterator.next();
//                if(type.isInstance(input)) {
//                    contained = true;
//                    break;
//                }
//            }
//
//            if(!contained) {
//                throw new UserError(this, "python_scripting.wrong_input", new Object[]{RendererService.getName(input.getClass()), this.getInputPorts().getPortNames()[index]});
//            }
//        }
//
//        return inputs;
//    }
}
