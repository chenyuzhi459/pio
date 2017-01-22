package io.sugo.pio.example.set;

import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.table.DataRow;
import io.sugo.pio.example.table.ExampleTable;
import io.sugo.pio.operator.Annotations;
import io.sugo.pio.operator.ViewModel;

import java.util.Iterator;


/**
 * This is a generic example set (view on the view stack of the data) which can be used to apply any
 * preprocessing model and create a view from it.
 * 
 * @author Sebastian Land, Ingo Mierswa
 */
public class ModelViewExampleSet extends AbstractExampleSet {

	private static final long serialVersionUID = -6443667708498013284L;

	private ExampleSet parent;

	private Attributes attributes;

	public ModelViewExampleSet(ExampleSet parent, ViewModel model) {
		this.parent = (ExampleSet) parent.clone();
		this.attributes = model.getTargetAttributes(parent);
	}

	/** Clone constructor. */
	public ModelViewExampleSet(ModelViewExampleSet other) {
		this.parent = (ExampleSet) other.parent.clone();
		this.attributes = other.attributes;

		if (other.attributes != null) {
			this.attributes = (Attributes) other.attributes.clone();
		}
	}

	@Override
	public Attributes getAttributes() {
		return this.attributes;
	}

	/**
	 * Creates a new example set reader.
	 */
	@Override
	public Iterator<Example> iterator() {
		return new AttributesExampleReader(parent.iterator(), this);
	}

	@Override
	public Example getExample(int index) {
		DataRow dataRow = this.parent.getExample(index).getDataRow();
		if (dataRow == null) {
			return null;
		} else {
			return new Example(dataRow, this);
		}
	}

	@Override
	public ExampleTable getExampleTable() {
		return this.parent.getExampleTable();
	}

	@Override
	public int size() {
		return this.parent.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.sugo.pio.operator.ResultObjectAdapter#getAnnotations()
	 */
	@Override
	public Annotations getAnnotations() {
		return parent.getAnnotations();
	}

	@Override
	public void cleanup() {
		parent.cleanup();
	}

}
