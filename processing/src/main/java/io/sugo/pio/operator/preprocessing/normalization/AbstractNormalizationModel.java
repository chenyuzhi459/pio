/**
 * Copyright (C) 2001-2016 by RapidMiner and the contributors
 *
 * Complete list of developers available at our web site:
 *
 * http://rapidminer.com
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */
package io.sugo.pio.operator.preprocessing.normalization;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.table.AttributeFactory;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorProgress;
import io.sugo.pio.operator.ProcessStoppedException;
import io.sugo.pio.operator.preprocessing.PreprocessingModel;
import io.sugo.pio.tools.Ontology;


/**
 * @author Sebastian Land
 * 
 */
public abstract class AbstractNormalizationModel extends PreprocessingModel {

	private static final long serialVersionUID = 9003091723155805502L;

	private static final int OPERATOR_PROGRESS_STEPS = 10_000;
	
	protected AbstractNormalizationModel(ExampleSet exampleSet) {
		super(exampleSet);

	}

	@Override
	public ExampleSet applyOnData(ExampleSet exampleSet) throws OperatorException {
		Attributes attributes = exampleSet.getAttributes();

		// constructing new attributes with generic names, holding old ones, if old type wasn't real
		Attribute[] oldAttributes = new Attribute[attributes.size()];
		int i = 0;
		for (Attribute attribute : attributes) {
			oldAttributes[i] = attribute;
			i++;
		}
		Attribute[] newAttributes = new Attribute[attributes.size()];
		for (i = 0; i < newAttributes.length; i++) {
			newAttributes[i] = oldAttributes[i];
			if (oldAttributes[i].isNumerical()) {
				if (!Ontology.ATTRIBUTE_VALUE_TYPE.isA(oldAttributes[i].getValueType(), Ontology.REAL)) {
					newAttributes[i] = AttributeFactory.createAttribute(Ontology.REAL);
					exampleSet.getExampleTable().addAttribute(newAttributes[i]);
					attributes.addRegular(newAttributes[i]);
				}
			}
		}

		// applying on data
		applyOnData(exampleSet, oldAttributes, newAttributes);

		// removing old attributes and change new attributes name to old ones if needed
		for (i = 0; i < oldAttributes.length; i++) {
			attributes.remove(oldAttributes[i]);
			// if attribute is new, then remove for later storing in correct order
			if (oldAttributes[i] != newAttributes[i]) {
				attributes.remove(newAttributes[i]);
			}
			attributes.addRegular(newAttributes[i]);
			newAttributes[i].setName(oldAttributes[i].getName());
		}

		return exampleSet;
	}

	/**
	 * This method must be implemented by the subclasses. Subclasses have to iterate over the
	 * exampleset and on each example iterate over the oldAttribute array and set the new values on
	 * the corresponding new attribute
	 * @throws ProcessStoppedException 
	 */
	protected void applyOnData(ExampleSet exampleSet, Attribute[] oldAttributes, Attribute[] newAttributes) throws ProcessStoppedException {
		// initialize progress
		OperatorProgress progress = null;
		if (getShowProgress() && getOperator() != null && getOperator().getProgress() != null) {
			progress = getOperator().getProgress();
			progress.setTotal(exampleSet.size());
		}
		int progressCounter = 0;
		
		// copying data
		for (Example example : exampleSet) {
			for (int i = 0; i < oldAttributes.length; i++) {
				if (oldAttributes[i].isNumerical()) {
					example.setValue(newAttributes[i], computeValue(oldAttributes[i], example.getValue(oldAttributes[i])));
				}
			}
			if (progress != null && ++progressCounter % OPERATOR_PROGRESS_STEPS == 0) {
				progress.setCompleted(progressCounter);
			}
		}
	}

	/**
	 * Subclasses might implement this methods in order to return a value if the implementation
	 * differs from the getValue() method for view creation. Otherwise this method just calls
	 * getValue(). If this method does not deliver enough informations, the subclass might override
	 * {@link #applyOnData(ExampleSet, Attribute[], Attribute[])}
	 */
	public double computeValue(Attribute attribute, double oldValue) {
		return getValue(attribute, oldValue);
	}

}
