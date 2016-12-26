package io.sugo.pio.example.table;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Attributes;
import io.sugo.pio.tools.Ontology;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;


/**
 * This is a possible abstract superclass for all attribute implementations. Most methods of
 * {@link Attribute} are already implemented here.
 *
 * @author Ingo Mierswa
 */
public abstract class AbstractAttribute implements Attribute {

	private static final long serialVersionUID = -9167755945651618227L;

	private transient List<Attributes> owners = new LinkedList<Attributes>();

	/** The current attribute construction description object. */
	private String constructionDescription = null;

	// --------------------------------------------------------------------------------

	/**
	 * Creates a simple attribute which is not part of a series and does not provide a unit string.
	 * This constructor should only be used for attributes which were not generated with help of a
	 * generator, i.e. this attribute has no function arguments. Only the last transformation is
	 * cloned, the other transformations are cloned by reference.
	 */
	protected AbstractAttribute(AbstractAttribute attribute) {
		// copy construction description
		this.constructionDescription = attribute.constructionDescription;
	}

	/**
	 * Creates a simple attribute which is not part of a series and does not provide a unit string.
	 * This constructor should only be used for attributes which were not generated with help of a
	 * generator, i.e. this attribute has no function arguments.
	 */
	protected AbstractAttribute(String name, int valueType) {
		this.constructionDescription = name;
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		if (owners == null) {
			owners = new LinkedList<Attributes>();
		}
	}

	@Override
	public void addOwner(Attributes attributes) {
		this.owners.add(attributes);
	}

	@Override
	public void removeOwner(Attributes attributes) {
		this.owners.remove(attributes);
	}

	/** Clones this attribute. */
	@Override
	public abstract Object clone();

	/**
	 * Returns true if the given attribute has the same name and the same table index.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof AbstractAttribute)) {
			return false;
		}
		AbstractAttribute a = (AbstractAttribute) o;
		return this.constructionDescription.equals(a.constructionDescription);
	}

	@Override
	public int hashCode() {
		return constructionDescription.hashCode();
	}

	/** Returns the name of the attribute. */
	@Override
	public String getName() {
		return this.constructionDescription;
	}

	/** Sets the name of the attribute. */
	@Override
	public void setName(String v) {
	}

	/** Returns the construction description. */
	@Override
	public void setConstruction(String description) {
		this.constructionDescription = description;
	}

	@Override
	public double getValue(DataRow row) {
		double result = row.get(0, 0);
		return result;
	}

	@Override
	public void setValue(DataRow row, double value) {
		double newValue = value;
		row.set(0, newValue, 0);
	}

	/**
	 * Returns the value type of this attribute.
	 *
	 * @see Ontology#ATTRIBUTE_VALUE_TYPE
	 */
	@Override
	public int getValueType() {
		return 0;
	}
}
