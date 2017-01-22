package io.sugo.pio.tools.expression;


import java.util.Objects;

/**
 * {@link FunctionInput} represents the input of a {@link FunctionDescription}. It contains a
 * category, an input name, input type (defined in {@link Ontology#VALUE_TYPE}) and input role
 * (defined in {@link Attributes}).
 *
 * @author Sabrina Kirstein
 * @since 6.5.0
 */
public class FunctionInput {

	public enum Category {
		SCOPE, CONSTANT, DYNAMIC;
	}

	/** name of the function input */
	private String name;

	/** type of the function input */
	private int type;

	/** additional information of the function input, for example role or macro value */
	private String additionalInformation;

	/** use custom icon ? */
	private boolean useCustomIcon;

	/** the category name under which the function input is shown */
	private String categoryName;

	/** category of the {@link FunctionInput}, used for syntax highlighting */
	private Category category;

	/** option to hide a constant in the UI but recognize it in the parser */
	private boolean invisible = false;

	/**
	 * Returns an instance of a function input with the given characteristics.
	 *
	 * @param category
	 *            category of the function input
	 * @param categoryName
	 *            category name of the function input
	 * @param name
	 *            name of the function input
	 * @param type
	 *            type of the function input
	 * @param additionalInformation
	 *            role of the function input
	 */
	public FunctionInput(Category category, String categoryName, String name, int type, String additionalInformation) {
		this(category, categoryName, name, type, additionalInformation, false);
	}

	/**
	 * Returns an instance of a function input with the given characteristics.
	 *
	 * @param category
	 *            category of the function input
	 * @param categoryName
	 *            category name of the function input
	 * @param name
	 *            name of the function input
	 * @param type
	 *            type of the function input
	 * @param additionalInformation
	 *            role of the function input
	 * @param customIcon
	 *            use a custom icon?
	 */
	public FunctionInput(Category category, String categoryName, String name, int type, String additionalInformation,
						 boolean customIcon) {
		this.category = category;
		this.categoryName = categoryName;
		this.name = name;
		this.type = type;
		this.additionalInformation = additionalInformation;
		this.useCustomIcon = customIcon;
	}

	/**
	 * Returns an instance of a function input with the given characteristics.
	 *
	 * @param category
	 *            category of the function input
	 * @param categoryName
	 *            category name of the function input
	 * @param name
	 *            name of the function input
	 * @param type
	 *            type of the function input
	 * @param additionalInformation
	 *            role of the function input
	 * @param customIcon
	 *            use a custom icon?
	 * @param invisible
	 *            option to hide a constant in the UI but recognize it in the parser
	 */
	public FunctionInput(Category category, String categoryName, String name, int type, String additionalInformation,
						 boolean customIcon, boolean invisible) {
		this(category, categoryName, name, type, additionalInformation, customIcon);
		this.invisible = invisible;
	}

	/**
	 * @return the name of the {@link FunctionInput}
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the category of the {@link FunctionInput}
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * @return the category name of the {@link FunctionInput}
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * @return the type of the {@link FunctionInput}
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the role of the {@link FunctionInput}
	 */
	public String getAdditionalInformation() {
		return additionalInformation;
	}

	/**
	 * Set additional information to information.
	 *
	 * @param information
	 */
	public void setAdditionalInformation(String information) {
		this.additionalInformation = information;
	}

	/**
	 * @return if this {@link FunctionInput} should use a custom icon
	 */
	public boolean useCustomIcon() {
		return useCustomIcon;
	}

	/**
	 * @return if this {@link FunctionInput} should be visible in the UI
	 */
	public boolean isVisible() {
		return !invisible;
	}

	@Override
	public int hashCode() {
		return Objects.hash(category, categoryName, name);
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (!(other instanceof FunctionInput)) {
			return false;
		}
		FunctionInput otherFunctionInput = (FunctionInput) other;
		return getCategory().equals(otherFunctionInput.getCategory())
		        && getCategoryName().equals(otherFunctionInput.getCategoryName())
		        && getName().equals(otherFunctionInput.getName());
	}

}
