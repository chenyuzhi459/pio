package io.sugo.pio.scripting.metadata;

import io.sugo.pio.operator.Operator;
import io.sugo.pio.ports.metadata.MDTransformationRule;
import io.sugo.pio.ports.metadata.SimpleProcessSetupError;

/**
 */
public class MetaDataCachingRule implements MDTransformationRule {
    private final Operator operator;
    private boolean operatorWorked;

    public MetaDataCachingRule(Operator operator) {
        this.operator = operator;
    }

    public void setOperatorWorked() {
        this.operatorWorked = true;
    }

    @Override
    public void transformMD() {
//        if(!cacherAdded && this.operator.getProcess() != null) {
//            this.addCacher();
//            this.cacherAdded = true;
//        }
//
//        if(cachedMetaData == null) {
//            this.operator.addError(new SimpleProcessSetupError(Severity.INFORMATION, operator.getPortOwner(), "python_scripting.metadata.cache_empty", new Object[0]));
//        } else {
//            List outputPorts = this.operator.getOutputPorts().getAllPorts();
//            int index = 0;
//
//            for(Iterator var3 = outputPorts.iterator(); var3.hasNext(); ++index) {
//                OutputPort port = (OutputPort)var3.next();
//                this.tryToDeliverMetaData(index, port);
//            }
//        }

    }
}
