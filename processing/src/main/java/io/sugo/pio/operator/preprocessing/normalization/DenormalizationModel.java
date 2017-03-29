package io.sugo.pio.operator.preprocessing.normalization;

import io.sugo.pio.example.*;
import io.sugo.pio.example.set.ExampleSetUtilities;
import io.sugo.pio.example.set.ExampleSetUtilities.SetsCompareOption;
import io.sugo.pio.example.set.ExampleSetUtilities.TypesCompareOption;
import io.sugo.pio.example.table.ViewAttribute;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.preprocessing.normalization.DenormalizationOperator.LinearTransformation;
import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.Tools;

import java.util.Iterator;
import java.util.Map;


/**
 * This Model can invert each possible linear transformation given by a normalization model.
 * 
 * @author Sebastian Land
 */
public class DenormalizationModel extends AbstractNormalizationModel {

	private static final long serialVersionUID = 1370670246351357686L;

	private Map<String, LinearTransformation> attributeTransformations;
	private AbstractNormalizationModel invertedModel;
	private boolean failOnMissing;

	protected DenormalizationModel(ExampleSet exampleSet, Map<String, LinearTransformation> attributeTransformations,
			AbstractNormalizationModel model) {
		this(exampleSet, attributeTransformations, model, false);
	}

	protected DenormalizationModel(ExampleSet exampleSet, Map<String, LinearTransformation> attributeTransformations,
			AbstractNormalizationModel model, boolean failOnMissingAttributes) {
		super(exampleSet);
		this.attributeTransformations = attributeTransformations;
		this.invertedModel = model;
		this.failOnMissing = failOnMissingAttributes;
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
			if (!attribute.isNumerical() || !attributeTransformations.containsKey(attribute.getName())) {
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
		LinearTransformation linearTransformation = attributeTransformations.get(targetAttribute.getName());
		if (linearTransformation != null) {
			return (value - linearTransformation.b) / linearTransformation.a;
		}
		return value;
	}

	@Override
	public String toResultString() {
		StringBuilder builder = new StringBuilder();

		builder.append("Denormalization Model of the following Normalization:" + Tools.getLineSeparator());
		builder.append(invertedModel.toResultString());

		return builder.toString();

	}

	@Override
	public ExampleSet applyOnData(ExampleSet exampleSet) throws OperatorException {
		if (failOnMissing) {
			ExampleSetUtilities.checkAttributesMatching(null, getTrainingHeader().getAttributes(),
					exampleSet.getAttributes(), SetsCompareOption.ALLOW_SUPERSET, TypesCompareOption.ALLOW_SAME_PARENTS);
		}
		return super.applyOnData(exampleSet);
	}

	public Map<String, LinearTransformation> getAttributeTransformations() {
		return attributeTransformations;
	}
	
	public AbstractNormalizationModel getInvertedModel() {
		return invertedModel;
	}
	
	public boolean shouldFailOnMissing() {
		return failOnMissing;
	}
	
}
