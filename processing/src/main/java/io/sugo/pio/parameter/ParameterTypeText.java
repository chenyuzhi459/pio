package io.sugo.pio.parameter;

/**
 * A parameter type for longer texts. In the GUI this might lead to a button opening a text editor.
 *
 * @author Ingo Mierswa
 */
public class ParameterTypeText extends ParameterTypeString {

	private static final long serialVersionUID = 8056689512740292084L;

	private static final String ATTRIBUTE_TEXT_TYPE = "text-type";

	private TextType type = TextType.PLAIN;

	private String templateText;

	/** Creates a new optional parameter type for longer texts. */
	public ParameterTypeText(String key, String description, TextType type) {
		super(key, description, true);
		setTextType(type);
	}

	/** Creates a new parameter type for longer texts with the given default value. */
	public ParameterTypeText(String key, String description, TextType type, String defaultValue) {
		super(key, description, defaultValue);
		setTextType(type);
	}

	/** Creates a new parameter type for longer texts. */
	public ParameterTypeText(String key, String description, TextType type, boolean optional) {
		super(key, description, optional);
		setTextType(type);
	}

	public void setTextType(TextType type) {
		this.type = type;
	}

	public TextType getTextType() {
		return this.type;
	}

	/**
	 * Sets the template text that is shown in the {@link TextPropertyDialog} if no text is set and
	 * no default value defined.
	 *
	 * @param templateText
	 *            the template text to show in the {@link TextPropertyDialog} if no text is set and
	 *            no default value defined
	 * @since 6.5
	 */
	public void setTemplateText(String templateText) {
		this.templateText = templateText;
	}

	/**
	 * Returns the template text that is shown in the {@link TextPropertyDialog} if no text is set
	 * and no default value defined.
	 *
	 * @return the template text
	 * @since 6.5
	 */
	public String getTemplateText() {
		return templateText;
	}
}
