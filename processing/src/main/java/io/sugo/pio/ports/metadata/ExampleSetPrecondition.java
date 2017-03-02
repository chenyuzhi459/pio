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

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.error.ProcessSetupError.Severity;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.tools.Ontology;


/**
 * @author Simon Fischer
 */
public class ExampleSetPrecondition extends AbstractPrecondition {

	private final String[] requiredSpecials;
	private final int allowedValueTypes;
	private final String[] ignoreForTypeCheck;
	private final int allowedSpecialsValueType;
	private final String[] requiredAttributes;
	private boolean optional = false;

	public ExampleSetPrecondition(InputPort inputPort) {
		this(inputPort, Ontology.ATTRIBUTE_VALUE, (String[]) null);
	}

	public ExampleSetPrecondition(InputPort inputPort, int allowedValueTypesForRegularAttributes, String... requiredSpecials) {
		this(inputPort, new String[0], allowedValueTypesForRegularAttributes, new String[0], Ontology.ATTRIBUTE_VALUE,
				requiredSpecials);
	}

	public ExampleSetPrecondition(InputPort inputPort, String[] requiredAttributeNames, int allowedValueTypesForRegular,
                                  String... requiredSpecials) {
		this(inputPort, requiredAttributeNames, allowedValueTypesForRegular, new String[0], Ontology.ATTRIBUTE_VALUE,
				requiredSpecials);
	}

	public ExampleSetPrecondition(InputPort inputPort, String requiredSpecials, int allowedValueTypForSpecial) {
		this(inputPort, new String[0], Ontology.ATTRIBUTE_VALUE, new String[0], allowedValueTypForSpecial, requiredSpecials);
	}

	public ExampleSetPrecondition(InputPort inputPort, String[] requiredAttributeNames, int allowedValueTypesForRegular,
                                  String[] ignoreForTypeCheck, int allowedValueTypesForSpecial, String... requiredSpecials) {
		super(inputPort);
		this.allowedValueTypes = allowedValueTypesForRegular;
		this.requiredSpecials = requiredSpecials;
		this.requiredAttributes = requiredAttributeNames;
		this.allowedSpecialsValueType = allowedValueTypesForSpecial;
		this.ignoreForTypeCheck = ignoreForTypeCheck;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	@Override
	public void assumeSatisfied() {
		getInputPort().receiveMD(new ExampleSetMetaData());
	}

	@Override
	public void check(MetaData metaData) {
		final InputPort inputPort = getInputPort();
		if (metaData == null) {
			if (!optional) {
				inputPort.addError(new InputMissingMetaDataError(inputPort, ExampleSet.class, null));
			} else {
				return;
			}
		} else {
			if (metaData instanceof ExampleSetMetaData) {
				ExampleSetMetaData emd = (ExampleSetMetaData) metaData;
				// checking attribute names
				for (String attributeName : requiredAttributes) {
					MetaDataInfo attInfo = emd.containsAttributeName(attributeName);
					if (attInfo == MetaDataInfo.NO) {
						createError(Severity.WARNING, "missing_attribute", attributeName);
					}
				}

				// checking allowed types
				if ((allowedValueTypes != Ontology.ATTRIBUTE_VALUE) && (allowedValueTypes != -1)) {
					for (AttributeMetaData amd : emd.getAllAttributes()) {
						if (amd.isSpecial()) {
							continue;
						}
						// check if name is in ignore list
						for (String name : ignoreForTypeCheck) {
							if (name.equals(amd.getName())) {
								continue;
							}
						}

						// otherwise do check
						if (!Ontology.ATTRIBUTE_VALUE_TYPE.isA(amd.getValueType(), allowedValueTypes)) {
							createError(Severity.ERROR, "regular_type_mismatch",
									new Object[] { Ontology.ATTRIBUTE_VALUE_TYPE.mapIndex(allowedValueTypes) });
							break;
						}
					}
				}

				// checking required special attribute roles
				if (requiredSpecials != null) {
					for (String name : requiredSpecials) {
						MetaDataInfo has = emd.hasSpecial(name);
						switch (has) {
							case NO:
								createError(Severity.ERROR, "special_missing", new Object[] { name });
								break;
							case UNKNOWN:
								createError(Severity.WARNING, "special_unknown", new Object[] { name });
								break;
							case YES:
								// checking type
								AttributeMetaData amd = emd.getSpecial(name);
								if (amd == null) {
									// TODO: This can happen for confidence. Then, hasSpecial
									// returns YES, but getSpecial(confidence) returns null
									break;
								}
								if (!Ontology.ATTRIBUTE_VALUE_TYPE.isA(amd.getValueType(), allowedSpecialsValueType)) {
									createError(Severity.ERROR, "special_attribute_has_wrong_type", amd.getName(), name,
											Ontology.ATTRIBUTE_VALUE_TYPE.mapIndex(allowedSpecialsValueType));
								}
								break;
						}
					}
				}
				try {
					makeAdditionalChecks(emd);
				} catch (UndefinedParameterError e) {
				}
			} else {
				inputPort.addError(new MetaDataUnderspecifiedError(inputPort));
			}
		}
	}

	/**
	 * Can be implemented by subclasses.
	 * 
	 * @throws UndefinedParameterError
	 */
	public void makeAdditionalChecks(ExampleSetMetaData emd) throws UndefinedParameterError {}

	@Override
	public String getDescription() {
		return "<em>expects:</em> ExampleSet";
	}

	@Override
	public boolean isCompatible(MetaData input, CompatibilityLevel level) {
		return null == input ? false : ExampleSet.class.isAssignableFrom(input.getObjectClass());
	}

	@Override
	public MetaData getExpectedMetaData() {
		return new ExampleSetMetaData();
	}
}
