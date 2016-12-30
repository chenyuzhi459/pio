///**
// * Copyright (C) 2001-2016 by RapidMiner and the contributors
// *
// * Complete list of developers available at our web site:
// *
// * http://rapidminer.com
// *
// * This program is free software: you can redistribute it and/or modify it under the terms of the
// * GNU Affero General Public License as published by the Free Software Foundation, either version 3
// * of the License, or (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
// * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// * Affero General Public License for more details.
// *
// * You should have received a copy of the GNU Affero General Public License along with this program.
// * If not, see http://www.gnu.org/licenses/.
// */
//package io.sugo.pio.tools;
//
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//
//import java.io.Serializable;
//
//
///**
// * A ParameterType holds information about type, range, and default value of a parameter. Lists of
// * ParameterTypes are provided by operators.
// * <p>
// * Sensitive information: <br/>
// * If your parameter is expected to never contain sensitive data, e.g. the number of iterations for
// * an algorithm, overwrite {@link #isSensitive()} and and return {@code false}. This method is used
// * to determine how parameters are handled in certain places, e.g. if their value is replaced before
// * uploading the process for operator recommendations. The default implementation always returns
// * {@code true}.
// * </p>
// *
// * @author Ingo Mierswa, Simon Fischer
// */
//public abstract class ParameterType implements Comparable<ParameterType>, Serializable {
//
//	private static final long serialVersionUID = 5296461242851710130L;
//
//	public static final String ELEMENT_PARAMETER_TYPE = "ParameterType";
//
//	private static final String ELEMENT_DESCRIPTION = "Description";
//
//	private static final String ELEMENT_CONDITIONS = "Conditions";
//
//	private static final String ATTRIBUTE_EXPERT = "is-expert";
//
//	private static final String ATTRIBUTE_DEPRECATED = "is-deprecated";
//
//	private static final String ATTRIBUTE_HIDDEN = "is-hidden";
//
//	private static final String ATTRIBUTE_OPTIONAL = "is-optional";
//
//	private static final String ATTRIBUTE_SHOW_RANGE = "is-range-shown";
//
//	private static final String ATTRIBUTE_KEY = "key";
//
//	private static final String ATTRIBUTE_CONDITION_CLASS = "condition-class";
//
//	private static final String ATTRIBUTE_CLASS = "class";
//
//	/** The key of this parameter. */
//	private String key;
//
//	/** The documentation. Used as tooltip text... */
//	private String description;
//
//	/**
//	 * Indicates if this is a parameter only viewable in expert mode. Mandatory parameters are
//	 * always viewable. The default value is true.
//	 */
//	private boolean expert = true;
//
//	/**
//	 * Indicates if this parameter is hidden and is not shown in the GUI. May be used in conjunction
//	 * with a configuration wizard which lets the user configure the parameter.
//	 */
//	private boolean isHidden = false;
//
//	/** Indicates if the range should be displayed. */
//	private boolean showRange = true;
//
//	/**
//	 * Indicates if this parameter is optional unless a dependency condition made it mandatory.
//	 */
//	private boolean isOptional = true;
//
//	/**
//	 * Indicates that this parameter is deprecated and remains only for compatibility reasons during
//	 * loading of older processes. It should neither be shown nor documented.
//	 */
//	private boolean isDeprecated = false;
//
//	/** Creates a new ParameterType. */
//	public ParameterType(String key, String description) {
//		this.key = key;
//		this.description = description;
//	}
//
//	public abstract Element getXML(String key, String value, boolean hideDefault, Document doc);
//
//	/** Returns a human readable description of the range. */
//	public abstract String getRange();
//
//	/** Returns a value that can be used if the parameter is not set. */
//	public abstract Object getDefaultValue();
//
//	/**
//	 * Returns the correct string representation of the default value. If the default is undefined,
//	 * it returns null.
//	 */
//	public String getDefaultValueAsString() {
//		return toString(getDefaultValue());
//	}
//
//	/** Sets the default value. */
//	public abstract void setDefaultValue(Object defaultValue);
//
//	/**
//	 * Returns true if the values of this parameter type are numerical, i.e. might be parsed by
//	 * {@link Double#parseDouble(String)}. Otherwise false should be returned. This method might be
//	 * used by parameter logging operators.
//	 */
//	public abstract boolean isNumerical();
//
//	public boolean showRange() {
//		return showRange;
//	}
//
//	public void setShowRange(boolean showRange) {
//		this.showRange = showRange;
//	}
//
//	/**
//	 * This method will be invoked by the Parameters after a parameter was set. The default
//	 * implementation is empty but subclasses might override this method, e.g. for a decryption of
//	 * passwords.
//	 */
//	public String transformNewValue(String value) {
//		return value;
//	}
//
//	/**
//	 * Returns true if this parameter can only be seen in expert mode. The default implementation
//	 * returns true if the parameter is optional. It is ensured that an non-optional parameter is
//	 * never expert!
//	 */
//	public boolean isExpert() {
//		return expert && isOptional;
//	}
//
//	/**
//	 * Sets if this parameter can be seen in expert mode (true) or beginner mode (false).
//	 *
//	 */
//	public void setExpert(boolean expert) {
//		this.expert = expert;
//	}
//
//
//	/**
//	 * This method indicates that this parameter is deprecated and isn't used anymore beside from
//	 * loading old process files.
//	 */
//	public void setDeprecated() {
//		this.isDeprecated = true;
//	}
//
//	/**
//	 * This sets if the parameter is optional or must be entered. If it is not optional, it may not
//	 * be an expert parameter and the expert status will be ignored!
//	 */
//	public final void setOptional(boolean isOptional) {
//		this.isOptional = isOptional;
//	}
//
//	/**
//	 * Checks whether the parameter is configured to be optional without looking at the parameter
//	 * conditions. This method can be invoked during  method
//	 * invocations as it does not check parameter conditions. It does not reflect the actual state
//	 * though as parameter conditions might change an optional parameter to become mandatory.
//	 *
//	 * @return whether the parameter is optional without checking the parameter conditions
//	 */
//	public final boolean isOptionalWithoutConditions() {
//		return isOptional;
//	}
//
//	/** Sets the key. */
//	public void setKey(String key) {
//		this.key = key;
//	}
//
//	/** Returns the key. */
//	public String getKey() {
//		return key;
//	}
//
//	/** Returns a short description. */
//	public String getDescription() {
//		return description;
//	}
//
//	/** Sets the short description. */
//	public void setDescription(String description) {
//		this.description = description;
//	}
//
//	/**
//	 * States whether a given parameter type implementation may contain sensitive information or
//	 * not. Sensitive information are obvious things like passwords, files, OAuth tokens, or
//	 * database connections. However less obvious things like SQL queries can also contain sensitive
//	 * information.
//	 *
//	 * @return always {@code true}
//	 */
//	public boolean isSensitive() {
//		return true;
//	}
//
//	/**
//	 * This method gives a hook for the parameter type to react on a renaming of an operator. It
//	 * must return the correctly modified String value. The default implementation does nothing.
//	 */
//	public String notifyOperatorRenaming(String oldOperatorName, String newOperatorName, String parameterValue) {
//		return parameterValue;
//	}
//
//	/** Returns a string representation of this value. */
//	public String toString(Object value) {
//		if (value == null) {
//			return "";
//		} else {
//			return value.toString();
//		}
//	}
//
//	@Override
//	public String toString() {
//		return key + " (" + description + ")";
//	}
//
//	@Override
//	public int compareTo(ParameterType o) {
//		if (!(o instanceof ParameterType)) {
//			return 0;
//		} else {
//			/* ParameterTypes are compared by key. */
//			return this.key.compareTo(o.key);
//		}
//	}
//}
