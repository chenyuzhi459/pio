package io.sugo.pio.example.set;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.table.ExampleTable;

import java.util.Iterator;


/**
 * This example set is a clone of the attributes without reference to any data. Therefore it can be
 * used as a data header description. Since no data reference exist, all example based methods will
 * throw an {@link UnsupportedOperationException}.
 * 
 */
public class HeaderExampleSet extends AbstractExampleSet {

	private static final long serialVersionUID = -255270841843010670L;

	/** The parent example set. */
	private Attributes attributes;

	public HeaderExampleSet(ExampleSet parent) {
		cloneAnnotationsFrom(parent);
		this.attributes = (Attributes) parent.getAttributes().clone();
	}

	/** Header example set clone constructor. */
	public HeaderExampleSet(HeaderExampleSet other) {
		cloneAnnotationsFrom(other);
		this.attributes = (Attributes) other.attributes.clone();
	}

	@Override
	public Attributes getAttributes() {
		return attributes;
	}

	@Override
	public Example getExample(int index) {
		return null;
	}

	@Override
	public ExampleTable getExampleTable() {
		throw new UnsupportedOperationException("The method getExampleTable() is not supported by the header example set.");
	}

	@Override
	@JsonProperty
	public int size() {
		return 0;
	}

	@Override
	public Iterator<Example> iterator() {
		throw new UnsupportedOperationException("The method iterator() is not supported by the header example set.");
	}
}
