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
package io.sugo.pio.ports.metadata;

import io.sugo.pio.example.Attributes;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorCapability;
import io.sugo.pio.operator.error.ProcessSetupError.Severity;
import io.sugo.pio.operator.learner.CapabilityProvider;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.tools.Ontology;


/**
 * This is a precondition for {@link InputPort}s that ensures that the capabilities given by an
 * operator are matched by the delivered ExampleSet.
 * 
 * @author Sebastian Land
 */
public class CapabilityPrecondition extends ExampleSetPrecondition {

	protected final CapabilityProvider capabilityProvider;

	public CapabilityPrecondition(CapabilityProvider capabilityProvider, InputPort inputPort) {
		super(inputPort);
		this.capabilityProvider = capabilityProvider;
	}

	@Override
	public void makeAdditionalChecks(ExampleSetMetaData metaData) {
		// regular attributes
		if (metaData.containsAttributesWithValueType(Ontology.NOMINAL, false) == MetaDataInfo.YES) {
			if (metaData.containsAttributesWithValueType(Ontology.BINOMINAL, false) == MetaDataInfo.YES) {
				if (!capabilityProvider.supportsCapability(OperatorCapability.BINOMINAL_ATTRIBUTES)) {
					createLearnerError(OperatorCapability.BINOMINAL_ATTRIBUTES.getDescription());
				}
			} else {
				if (!capabilityProvider.supportsCapability(OperatorCapability.POLYNOMINAL_ATTRIBUTES)) {
					createLearnerError(OperatorCapability.POLYNOMINAL_ATTRIBUTES.getDescription());
				}
			}
		}
		if (metaData.containsAttributesWithValueType(Ontology.NUMERICAL, false) == MetaDataInfo.YES
				&& !capabilityProvider.supportsCapability(OperatorCapability.NUMERICAL_ATTRIBUTES)) {
			createLearnerError(OperatorCapability.NUMERICAL_ATTRIBUTES.getDescription());
		}

		checkLabelPreconditions(metaData);

		// weighted examples
		if (!capabilityProvider.supportsCapability(OperatorCapability.WEIGHTED_EXAMPLES)) {
			switch (metaData.hasSpecial(Attributes.WEIGHT_NAME)) {
				case YES:
					createError(Severity.WARNING, "learner_does_not_support_weights");
					break;
				case NO:
				case UNKNOWN:
				default:
					break;
			}
		}

		// missing values
		if (!capabilityProvider.supportsCapability(OperatorCapability.MISSING_VALUES)) {
			if (metaData.getAllAttributes() != null) {
				for (AttributeMetaData amd : metaData.getAllAttributes()) {
					if (!amd.isSpecial() || Attributes.LABEL_NAME.equals(amd.getRole())) {
						if (amd.containsMissingValues() == MetaDataInfo.YES) {
							createLearnerError(OperatorCapability.MISSING_VALUES.getDescription());
							break;
						}
					}
				}
			}
		}
	}

	protected void checkLabelPreconditions(ExampleSetMetaData metaData) {
		// label
		// check if needs label
		// TODO: This checks if it is supported, but not if it is required. This test will break if
		// we add a new label type
		// because it will then be incomplete.
		if (!capabilityProvider.supportsCapability(OperatorCapability.NO_LABEL)) {
			// if it supports no label it's simply irrelevant if label is present.
			if (capabilityProvider.supportsCapability(OperatorCapability.ONE_CLASS_LABEL)
					|| capabilityProvider.supportsCapability(OperatorCapability.BINOMINAL_LABEL)
					|| capabilityProvider.supportsCapability(OperatorCapability.POLYNOMINAL_LABEL)
					|| capabilityProvider.supportsCapability(OperatorCapability.NUMERICAL_LABEL)) {
				switch (metaData.hasSpecial(Attributes.LABEL_NAME)) {
					case UNKNOWN:
						getInputPort().addError(
								new SimpleMetaDataError(Severity.WARNING, getInputPort(),
										"pio.error.metadata.special_unknown", new Object[] { Attributes.LABEL_NAME }));
						break;
					case NO:
						getInputPort().addError(
								new SimpleMetaDataError(Severity.ERROR, getInputPort(),
										"pio.error.metadata.special_missing", new Object[] { Attributes.LABEL_NAME }));
						break;
					case YES:
						AttributeMetaData label = metaData.getLabelMetaData();
						if (label.isNominal()) {
							if (capabilityProvider.supportsCapability(OperatorCapability.ONE_CLASS_LABEL)
									&& !capabilityProvider.supportsCapability(OperatorCapability.BINOMINAL_LABEL)
									&& !capabilityProvider.supportsCapability(OperatorCapability.POLYNOMINAL_LABEL)) {

								// if it only supports one class label
								if (label.getValueSet().size() > 1 && label.getValueSetRelation() != SetRelation.UNKNOWN) {
									createError(Severity.ERROR, "one_class_label_invalid", label.getValueSetRelation()
											.toString() + " " + label.getValueSet().size());
								}
							} else {
								// if it supports two or more classes
								if (label.getValueSet().size() == 1 && label.getValueSetRelation() == SetRelation.EQUAL) {
									createError(Severity.ERROR, "no_polynomial_label");

								} else {
									// if two or more classes are present
									if (label.isBinominal() || label.getValueSetRelation() == SetRelation.EQUAL
											&& label.getValueSet().size() == 2) {
										if (!capabilityProvider.supportsCapability(OperatorCapability.BINOMINAL_LABEL)) {
											createLearnerError(OperatorCapability.BINOMINAL_LABEL.getDescription());
										}
									} else {
										if (!capabilityProvider.supportsCapability(OperatorCapability.POLYNOMINAL_LABEL)) {

											/*
											 * check if not nominal label is in fact a binominal
											 * label
											 */
											if (label.getValueSetRelation() != SetRelation.EQUAL
													|| label.getValueSet().size() != 2) {
												createLearnerError(OperatorCapability.POLYNOMINAL_LABEL.getDescription());
											} else {
												createLearnerError(OperatorCapability.BINOMINAL_LABEL.getDescription());
											}
										}
									}
								}
							}
						} else if (label.isNumerical()
								&& !capabilityProvider.supportsCapability(OperatorCapability.NUMERICAL_LABEL)) {
							createLearnerError(OperatorCapability.NUMERICAL_LABEL.getDescription());
						}
				}
			}
		}
	}

	protected void createLearnerError(String description) {
		String id = "Learner";
		if (capabilityProvider instanceof Operator) {
			id = ((Operator) capabilityProvider).getName();
		}
		createError(Severity.ERROR, "learner_cannot_handle", new Object[] { id, description });
	}
}
