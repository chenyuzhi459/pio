package io.sugo.pio.operator.io;

import java.util.Observable;
import java.util.Observer;


/**
 * The meta data either guessed by RapidMiner or specified by the user for a column of an excel
 * file, csv file, etc.
 * 
 * @author Simon Fischer
 * 
 */
public class ColumnMetaData extends Observable {

	/** Attribute name as specified in the file, or generated by the source. */
	private String originalAttributeName;
	/** Attribute name as specified by the user. */
	private String userDefinedAttributeName;
	private int attributeValueType;
	private String role;
	private boolean selected;

	public ColumnMetaData() {

	}

	public ColumnMetaData(String originalAttributeName, String userDefinedAttributeName, int attributeValueType,
						  String role, boolean selected) {
		super();
		this.originalAttributeName = originalAttributeName;
		this.userDefinedAttributeName = userDefinedAttributeName;
		this.attributeValueType = attributeValueType;
		this.role = role;
		this.selected = selected;
	}

	/** Used to inform the validator about the ColumnMetaData object **/
	@Override
	public synchronized void addObserver(Observer o) {
		super.addObserver(o);
	}

	public String getOriginalAttributeName() {
		return originalAttributeName;
	}

	public void setOriginalAttributeName(String originalAttributeName) {
		if (equals(this.originalAttributeName, originalAttributeName)) {
			return;
		}
		this.originalAttributeName = originalAttributeName;
		setChanged();
		notifyObservers();
	}

	public String getUserDefinedAttributeName() {
		return userDefinedAttributeName;
	}

	public void setUserDefinedAttributeName(String userDefinedAttributeName) {
		if (equals(this.userDefinedAttributeName, userDefinedAttributeName)) {
			return;
		}
		this.userDefinedAttributeName = userDefinedAttributeName;
		setChanged();
		notifyObservers();
	}

	public int getAttributeValueType() {
		return attributeValueType;
	}

	public void setAttributeValueType(int attributeValueType) {
		if (attributeValueType == this.attributeValueType) {
			return;
		}
		this.attributeValueType = attributeValueType;
		setChanged();
		notifyObservers();
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		if (equals(role, this.role)) {
			return;
		}
		this.role = role;
		setChanged();
		notifyObservers();
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		setChanged();
		notifyObservers();
	}

	@Override
	public String toString() {
		return getRole() + " " + getUserDefinedAttributeName() + " (" + getOriginalAttributeName() + ")"
				+ getAttributeValueType() + " " + (isSelected() ? "x" : "-");
	}

//	public AttributeMetaData getAttributeMetaData() {
//		String roleId = getRole();
//		if (!Attributes.ATTRIBUTE_NAME.equals(roleId)) {
//			return new AttributeMetaData(getUserDefinedAttributeName(), getAttributeValueType(), roleId);
//		} else {
//			return new AttributeMetaData(getUserDefinedAttributeName(), getAttributeValueType());
//		}
//	}

	/** Returns whether the user specified a name different from the default. */
	public boolean isAttributeNameSpecified() {
		if (userDefinedAttributeName == null) {
			return false;
		}
		return !userDefinedAttributeName.equals(originalAttributeName);
	}

	private static boolean equals(String s1, String s2) {
		return ((s1 == null) && (s2 == null)) || ((s1 != null) && s1.equals(s2));
	}
}