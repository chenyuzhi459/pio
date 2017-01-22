package io.sugo.pio.operator.io;


import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.ports.metadata.MetaData;

/**
 * Super class of all operators requiring no input and creating an {@link ExampleSet}.
 *
 * @author Simon Fischer
 */
public abstract class AbstractExampleSource extends AbstractReader<ExampleSet> {

	public AbstractExampleSource() {
		super(ExampleSet.class);
	}

	@Override
	public MetaData getGeneratedMetaData() throws OperatorException {
		return new ExampleSetMetaData();
	}

	/** Creates (or reads) the ExampleSet that will be returned by {@link #apply()}. */
	public abstract ExampleSet createExampleSet() throws OperatorException;

	@Override
	public ExampleSet read() throws OperatorException {
		return createExampleSet();
	}

}
