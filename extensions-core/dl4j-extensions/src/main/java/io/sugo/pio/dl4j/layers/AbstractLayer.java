package io.sugo.pio.dl4j.layers;

import io.sugo.pio.dl4j.io.LayerSemaphore;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.PortType;
import org.deeplearning4j.nn.conf.layers.Layer;

/**
 */
public abstract class AbstractLayer extends Operator {

    private final InputPort inPort = getInputPorts().createPort(PortType.THROUGH);
    private final OutputPort outPort = getOutputPorts().createPort(PortType.THROUGH);

    public abstract Layer getLayer();

    public abstract Layer getLayer(int i);

    public boolean isLinked() {
        LayerSemaphore semaphore = inPort.getDataOrNull(LayerSemaphore.class);
        if (semaphore != null && semaphore.getClass() == LayerSemaphore.class){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void doWork() {
        super.doWork();
        outPort.deliver(inPort.getDataOrNull(LayerSemaphore.class));
    }

    public abstract int getNumNodes();
}
