package io.sugo.pio.parameter;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.ports.metadata.MetaData;
import io.sugo.pio.ports.metadata.ModelMetaData;
import io.sugo.pio.tools.Ontology;

import java.util.Collections;
import java.util.Vector;

/**
 * This attribute type supports the user by let him select an attribute name from a combo box of
 * known attribute names. For long lists, auto completion and filtering of the drop down menu eases
 * the handling. For knowing attribute names before process execution a valid meta data
 * transformation must be performed. Otherwise the user might type in the name, instead of choosing.
 *
 * @author Sebastian Land
 */
public class ParameterTypeAttribute extends ParameterTypeString {

	private String defaultValue = null;

	/**
	 * A {@link MetaDataProvider} which provides metadata by querying the provided input port.
	 * It is used by the Web Client to retrieve input port data for attribute parameters.
	 *
	 */
	public static final class InputPortMetaDataProvider implements MetaDataProvider {

		private final InputPort inPort;

		private InputPortMetaDataProvider(InputPort inPort) {
			this.inPort = inPort;
		}

		@JsonProperty
		@Override
		public MetaData getMetaData() {
			if (inPort != null) {
				return inPort.getMetaData();
			} else {
				return new MetaData();
			}
		}

		public InputPort getInputPort() {
			return inPort;
		}
	}

	private static final long serialVersionUID = -4177652183651031337L;

	private static final String ELEMENT_ALLOWED_TYPES = "AllowedTypes";

	private static final String ELEMENT_ALLOWED_TYPE = "Type";

	// private transient InputPort inPort;
	@JsonProperty
	private MetaDataProvider metaDataProvider;

	private int[] allowedValueTypes;

	public ParameterTypeAttribute(final String key, String description, InputPort inPort) {
		this(key, description, inPort, false);
	}

	public ParameterTypeAttribute(final String key, String description, InputPort inPort, int... valueTypes) {
		this(key, description, inPort, false, valueTypes);
	}

	public ParameterTypeAttribute(final String key, String description, InputPort inPort, boolean optional) {
		this(key, description, inPort, optional, Ontology.ATTRIBUTE_VALUE);
	}

	public ParameterTypeAttribute(final String key, String description, InputPort inPort, boolean optional, boolean expert) {
		this(key, description, inPort, optional, Ontology.ATTRIBUTE_VALUE);
	}

	public ParameterTypeAttribute(final String key, String description, InputPort inPort, boolean optional, boolean expert,
								  int... valueTypes) {
		this(key, description, inPort, optional, Ontology.ATTRIBUTE_VALUE);
		allowedValueTypes = valueTypes;
	}

	public ParameterTypeAttribute(final String key, String description, final InputPort inPort, boolean optional,
								  int... valueTypes) {
		this(key, description, new InputPortMetaDataProvider(inPort), optional, valueTypes);
	}

	public ParameterTypeAttribute(final String key, String description, MetaDataProvider metaDataProvider, boolean optional,
								  int... valueTypes) {
		super(key, description, "");
		this.metaDataProvider = metaDataProvider;
		allowedValueTypes = valueTypes;
	}

	public Vector<String> getAttributeNames() {
		Vector<String> names = new Vector<>();
		Vector<String> regularNames = new Vector<>();

		MetaData metaData = getMetaData();
		if (metaData != null) {
			if (metaData instanceof ExampleSetMetaData) {
				ExampleSetMetaData emd = (ExampleSetMetaData) metaData;
				for (AttributeMetaData amd : emd.getAllAttributes()) {
					if (!isFilteredOut(amd) && isOfAllowedType(amd.getValueType())) {
						if (amd.isSpecial()) {
							names.add(amd.getName());
						} else {
							regularNames.add(amd.getName());
						}
					}

				}
			} else if (metaData instanceof ModelMetaData) {
				ModelMetaData mmd = (ModelMetaData) metaData;
				ExampleSetMetaData emd = mmd.getTrainingSetMetaData();
				if (emd != null) {
					for (AttributeMetaData amd : emd.getAllAttributes()) {
						if (!isFilteredOut(amd) && isOfAllowedType(amd.getValueType())) {
							if (amd.isSpecial()) {
								names.add(amd.getName());
							} else {
								regularNames.add(amd.getName());
							}
						}
					}
				}
			}
		}
		Collections.sort(names);
		Collections.sort(regularNames);
		names.addAll(regularNames);

		return names;
	}

	private boolean isOfAllowedType(int valueType) {
		boolean isAllowed = false;
		for (int type : allowedValueTypes) {
			isAllowed |= Ontology.ATTRIBUTE_VALUE_TYPE.isA(valueType, type);
		}
		return isAllowed;
	}

	@Override
	public Object getDefaultValue() {
		return "";
	}

	/** Returns false. */
	@Override
	public boolean isNumerical() {
		return false;
	}

	@Override
	public String getRange() {
		return "string" + (defaultValue != null ? "; default: '" + defaultValue + "'" : "");
	}

	/**
	 * This method might be overridden by subclasses in order to select attributes which are
	 * applicable
	 */
	protected boolean isFilteredOut(AttributeMetaData amd) {
		return false;
	};

	// public InputPort getInputPort() {
	// return inPort;
	// }
	public MetaDataProvider getMetaDataProvider() {
		return metaDataProvider;
	}

	/** Returns the meta data currently available by the {@link #metaDataProvider}. */
	public MetaData getMetaData() {
		MetaData metaData = null;
		if (metaDataProvider != null) {
			metaData = metaDataProvider.getMetaData();
		}
		return metaData;
	}
}
