package io.sugo.pio.operator.io;

import io.sugo.pio.constant.PortConstant;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.metadata.MetaData;
import io.sugo.pio.ports.metadata.PassThroughRule;
import io.sugo.pio.ports.metadata.SimplePrecondition;
import io.sugo.pio.tools.io.Encoding;

import java.util.LinkedList;
import java.util.List;


/**
 * Superclass of all operators that take a single object as input, save it to disk and return the
 * same object as output. This class is mainly a tribute to the e-LICO DMO.
 * 
 * It defines precondition and a pass through rule for its output port.
 * 
 * @author Simon Fischer
 */
public abstract class AbstractWriter<T extends IOObject> extends Operator {

	private InputPort inputPort = getInputPorts().createPort(PortConstant.INPUT, PortConstant.INPUT_DESC);
	private OutputPort outputPort = getOutputPorts().createPort(PortConstant.THROUGH, PortConstant.THROUGH_DESC);
	private Class<T> savedClass;

	public AbstractWriter(Class<T> savedClass) {
		super();
		this.savedClass = savedClass;
		inputPort.addPrecondition(new SimplePrecondition(inputPort, new MetaData(savedClass)));
		getTransformer().addRule(new PassThroughRule(inputPort, outputPort, false));
	}

	/**
	 * Creates (or reads) the ExampleSet that will be returned by {@link #apply()}.
	 * 
	 * @return the written IOObject itself
	 */
	public abstract T write(T ioobject) throws OperatorException;

	@Override
	public final void doWork() throws OperatorException {
		T ioobject = inputPort.getData(savedClass);
		ioobject = write(ioobject);
		outputPort.deliver(ioobject);
	}

	protected boolean supportsEncoding() {
		return false;
	}

	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = new LinkedList<ParameterType>();
		types.addAll(super.getParameterTypes());
		if (supportsEncoding()) {
			types.addAll(Encoding.getParameterTypes(this));
		}
		return types;
	}
}
