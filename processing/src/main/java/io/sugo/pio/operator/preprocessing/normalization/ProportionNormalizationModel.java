package io.sugo.pio.operator.preprocessing.normalization;

import io.sugo.pio.example.*;
import io.sugo.pio.example.table.ViewAttribute;
import io.sugo.pio.tools.Ontology;

import java.util.*;
import java.util.Map.Entry;


/**
 * This model is able to transform the data in a way, every transformed attribute of an example
 * contains the proportion of the total sum of this attribute over all examples.
 * 
 * @author Sebastian Land
 */
public class ProportionNormalizationModel extends AbstractNormalizationModel {

	private static final long serialVersionUID = 5620317015578777169L;

	private HashMap<String, Double> attributeSums;
	private Set<String> attributeNames;

	/** Create a new normalization model. */
	public ProportionNormalizationModel(ExampleSet exampleSet, HashMap<String, Double> attributeSums) {
		super(exampleSet);
		this.attributeSums = attributeSums;
		attributeNames = new HashSet<String>();
		for (Attribute attribute : exampleSet.getAttributes()) {
			if (attribute.isNumerical()) {
				attributeNames.add(attribute.getName());
			}
		}
	}

	@Override
	public Attributes getTargetAttributes(ExampleSet viewParent) {
		SimpleAttributes attributes = new SimpleAttributes();
		// add special attributes to new attributes
		Iterator<AttributeRole> roleIterator = viewParent.getAttributes().allAttributeRoles();
		while (roleIterator.hasNext()) {
			AttributeRole role = roleIterator.next();
			if (role.isSpecial()) {
				attributes.add(role);
			}
		}
		// add regular attributes
		for (Attribute attribute : viewParent.getAttributes()) {
			if (!attribute.isNumerical() || !attributeNames.contains(attribute.getName())) {
				attributes.addRegular(attribute);
			} else {
				// giving new attributes old name: connection to rangesMap
				attributes.addRegular(new ViewAttribute(this, attribute, attribute.getName(), Ontology.NUMERICAL, null));
			}
		}
		return attributes;
	}

	@Override
	public double getValue(Attribute targetAttribute, double value) {
		Double sum = attributeSums.get(targetAttribute.getName());
		return (value / sum);
	}

	/**
	 * Returns a nicer name. Necessary since this model is defined as inner class.
	 */
	@Override
	public String getName() {
		return "Proportional normalization model";
	}

	/** Returns a string representation of this model. */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Normalizes all attributes proportional to their respective total sum. Attributes sums: \n");
		for (Entry<String, Double> entry : attributeSums.entrySet()) {
			buffer.append(entry.getKey() + ": " + entry.getValue().doubleValue() + "\n");
		}
		return buffer.toString();
	}
	
	public Set<String> getAttributeNames() {
		return attributeNames;
	}
	
	public Map<String, Double> getAttributeSums() {
		return attributeSums;
	}

}
