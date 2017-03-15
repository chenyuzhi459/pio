package io.sugo.pio.dl4j.layers;

import io.sugo.pio.dl4j.io.LayerSemaphore;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.PortType;
import io.sugo.pio.ports.metadata.PassThroughRule;
import org.deeplearning4j.nn.conf.layers.Layer;

/**
 */
public abstract class AbstractLayer extends Operator {

    private final InputPort inPort = getInputPorts().createPort(PortType.THROUGH);
    private final OutputPort outPort = getOutputPorts().createPort(PortType.THROUGH);

    public AbstractLayer() {
        getTransformer().addRule(new PassThroughRule(inPort, outPort, false));
        getTransformer().addGenerationRule(outPort, LayerSemaphore.class);
    }

    public abstract Layer getLayer() throws UndefinedParameterError;

    public abstract Layer getLayer(int i) throws UndefinedParameterError;

    public boolean isLinked() throws UserError {

        LayerSemaphore semaphore = inPort.getDataOrNull(LayerSemaphore.class);
        if (semaphore != null && semaphore.getClass() == LayerSemaphore.class){
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void doWork() throws OperatorException {
        super.doWork();
        outPort.deliver(inPort.getDataOrNull(LayerSemaphore.class));
    }

    public abstract int getNumNodes() throws UndefinedParameterError;

    public abstract String getLayerName();

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.algorithmModel;
    }

}
