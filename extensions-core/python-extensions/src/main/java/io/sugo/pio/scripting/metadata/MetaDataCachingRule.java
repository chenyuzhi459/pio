package io.sugo.pio.scripting.metadata;

import io.sugo.pio.OperatorProcess;
import io.sugo.pio.ProcessStateListener;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.error.ProcessSetupError;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.metadata.MDTransformationRule;
import io.sugo.pio.ports.metadata.MetaData;
import io.sugo.pio.ports.metadata.SimpleMetaDataError;
import io.sugo.pio.ports.metadata.SimpleProcessSetupError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 */
public class MetaDataCachingRule implements MDTransformationRule {
    private final Operator operator;
    private List<MetaData> cachedMetaData;
    private Set<Integer> noDataCache;
    private boolean cacherAdded;
    private boolean cachedInProcess;
    private boolean operatorWorked;

    public MetaDataCachingRule(Operator operator) {
        this.operator = operator;
    }

    public void setOperatorWorked() {
        this.operatorWorked = true;
    }

    @Override
    public void transformMD() {
//        if ((!this.cacherAdded) && (this.operator.getProcess() != null)) {
//            addCacher();
            cacheMetaData();
//            this.cacherAdded = true;
//        }
        int index;
        if (this.cachedMetaData == null) {
            this.operator.addError(new SimpleProcessSetupError(ProcessSetupError.Severity.INFORMATION,
                    this.operator.getInputPorts().getOwner(), "pio.error.metadata.python_scripting.metadata.cache_empty", new Object[0]));
        } else {
            List<OutputPort> outputPorts = this.operator.getOutputPorts().getAllPorts();
            index = 0;
            for (OutputPort port : outputPorts) {
                tryToDeliverMetaData(index, port);
                index++;
            }
        }
    }

    private void tryToDeliverMetaData(int index, OutputPort port) {
        if (port.isConnected()) {
            if (index < this.cachedMetaData.size()) {
                MetaData md = this.cachedMetaData.get(index);
                if (md != null) {
                    port.deliverMD(md);
                } else if ((this.noDataCache != null) && (this.noDataCache.contains(Integer.valueOf(index)))) {
                    this.operator.addError(new SimpleMetaDataError(ProcessSetupError.Severity.WARNING, port,
                            "pio.error.metadata.python_scripting.no_data", new Object[0]));
                } else {
                    this.operator.addError(new SimpleMetaDataError(ProcessSetupError.Severity.INFORMATION, port,
                            "pio.error.metadata.python_scripting.cache_empty", new Object[0]));
                }
            } else {
                this.operator.addError(new SimpleMetaDataError(ProcessSetupError.Severity.INFORMATION, port,
                        "pio.error.metadata.python_scripting.cache_empty", new Object[0]));
            }
        }
    }

    private void addCacher() {
        this.operator.getProcess().addProcessStateListener(new ProcessStateListener() {
            public void stopped(OperatorProcess process) {
                if (!MetaDataCachingRule.this.cachedInProcess) {
                    MetaDataCachingRule.this.cacheMetaData();
                }
            }

            public void started(OperatorProcess process) {
                MetaDataCachingRule.this.cachedInProcess = false;
                MetaDataCachingRule.this.operatorWorked = false;
            }

            public void resumed(OperatorProcess process) {
            }

            public void paused(OperatorProcess process) {
                if ((MetaDataCachingRule.this.operatorWorked) && (!MetaDataCachingRule.this.cachedInProcess)) {
                    MetaDataCachingRule.this.cacheMetaData();
                    MetaDataCachingRule.this.cachedInProcess = true;
                }
            }
        });
    }

    private void cacheMetaData() {
        this.cachedMetaData = new ArrayList();
        List<OutputPort> outputPorts = this.operator.getOutputPorts().getAllPorts();
        this.noDataCache = new HashSet(outputPorts.size());
        int index = 0;
        for (OutputPort port : outputPorts) {
            IOObject object = port.getAnyDataOrNull();
            if (object != null) {
                this.cachedMetaData.add(MetaData.forIOObject(object));
            } else {
                if (port.isConnected()) {
                    this.noDataCache.add(Integer.valueOf(index));
                }
                this.cachedMetaData.add(null);
            }
            index++;
        }
//        RapidMinerGUI.getMainFrame().validateProcess(true);
    }
}
