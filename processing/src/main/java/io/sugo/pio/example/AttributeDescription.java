package io.sugo.pio.example;

import io.sugo.pio.tools.Ontology;

import java.io.Serializable;


/**
 * This class holds all basic information about an attribute. This is useful since a cloned
 * attribute can simply use the same reference to a description object wihtout the need of cloning
 * all information. This reduces the amount of used memory due to attribute clones.
 *
 */
public class AttributeDescription implements Serializable {

	private static final long serialVersionUID = 8641898727515830321L;

	/** The name of the attribute. */
	private String name;

	/**
	 * An int indicating the value type in terms of the Ontology.ATTRIBUTE_VALUE_TYPE.
	 */
	private int valueType = Ontology.ATTRIBUTE_VALUE;

	/**
	 * An int indicating the block type in terms of the Ontology.ATTRIBUTE_BLOCK_TYPE.
	 */
	private int blockType = Ontology.ATTRIBUTE_BLOCK;

	/** The default value for this Attribute. */
	private double defaultValue = 0.0;

	/** Index of this attribute in its ExampleTable. */
	private int index = Attribute.UNDEFINED_ATTRIBUTE_INDEX;

	public AttributeDescription(Attribute attribute, String name, int valueType, int blockType, double defaultValue,
                                int tableIndex) {
		this.name = name;
		this.valueType = valueType;
		this.blockType = blockType;
		this.defaultValue = defaultValue;
		this.index = tableIndex;

	}

	private AttributeDescription(AttributeDescription other) {
		this.name = other.name;
		this.valueType = other.valueType;
		this.blockType = other.blockType;
		this.defaultValue = other.defaultValue;
		this.index = other.index;
	}

	@Override
	public Object clone() {
		return new AttributeDescription(this);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String newName) {
		this.name = newName;
	}

	public int getValueType() {
		return this.valueType;
	}

	public int getBlockType() {
		return this.blockType;
	}

	public void setBlockType(int b) {
		this.blockType = b;
	}

	public double getDefault() {
		return this.defaultValue;
	}

	public void setDefault(double defaultValue) {
		this.defaultValue = defaultValue;
	}

	public int getTableIndex() {
		return this.index;
	}

	public void setTableIndex(int i) {
		this.index = i;
	}

	/**
	 * Returns true if the given attribute has the same name and the same table index.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof AttributeDescription)) {
			return false;
		}
		AttributeDescription a = (AttributeDescription) o;
		if (this.index != a.getTableIndex()) {
			return false;
		}
		if (!this.name.equals(a.getName())) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return name.hashCode() ^ this.index;
	}
}
