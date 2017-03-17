package io.sugo.pio.parameter;

/**
 * A parameter type for files. Operators ask for the selected file with
 * {@link io.sugo.pio.operator.Operator#getParameterAsFile(String)}. The extension should be
 * defined without the point (separator).
 *
 */
public class ParameterTypeCsvFile extends ParameterTypeString {

	private static final String ATTRIBUTE_EXTENSION_ELEMENT = "extension";
	private static final String ATTRIBUTE_EXTENSION_ATTRIBUTE = "value";

	private String[] extensions = null;
	private boolean addAllFileExtensionsFilter = false;

	/**
	 * Creates a new parameter type for files with the given extension. If the extension is null no
	 * file filters will be used. If the parameter is not optional, it is set to be not expert.
	 */
	public ParameterTypeCsvFile(String key, String description, boolean optional, String[] extensions) {
		super(key, description, null);
		setOptional(optional);
		this.extensions = extensions;
	}

	/**
	 * Creates a new parameter type for files with the given extension. If the extension is null no
	 * file filters will be used. If the parameter is not optional, it is set to be not expert.
	 */
	public ParameterTypeCsvFile(String key, String description, String extension, boolean optional) {
		super(key, description, null);
		setOptional(optional);
		this.extensions = new String[] { extension };
	}

	/**
	 * Creates a new parameter type for file with the given extension. If the extension is null no
	 * file filters will be used. The parameter will be optional.
	 */
	public ParameterTypeCsvFile(String key, String description, String extension, String defaultFileName) {
		super(key, description, defaultFileName);
		this.extensions = new String[] { extension };
	}

	public ParameterTypeCsvFile(String key, String description, String extension, boolean optional, boolean expert) {
		this(key, description, extension, optional);
	}

	public String getExtension() {
		return extensions[0];
	}

	public String[] getExtensions() {
		return extensions;
	}

	public String[] getKeys() {
		String[] keys = new String[extensions.length];
		for (int i = 0; i < extensions.length; i++) {
			keys[i] = getKey();
		}
		return keys;
	}

	public void setExtension(String extension) {
		this.extensions[0] = extension;
	}

	public void setExtensions(String[] extensions) {
		this.extensions = extensions;
	}

	/**
	 * @param addAllFileFormatsFilter
	 *            defines whether a filter for all file extension should be added as default filter
	 *            for the file chooser dialog. This makes most sense for file reading operations
	 *            that allow to read files with multiple file extensions. For file writing
	 *            operations it is not recommended as the new filter will not add the correct file
	 *            ending when entering the path of a file that does not exist.
	 */
	public void setAddAllFileExtensionsFilter(boolean addAllFileFormatsFilter) {
		this.addAllFileExtensionsFilter = addAllFileFormatsFilter;
	}

	/**
	 * @return whether a filter all with supported file extensions should be added as default filter
	 *         for the file chooser dialog
	 */
	public boolean isAddAllFileExtensionsFilter() {
		return addAllFileExtensionsFilter;
	}

	@Override
	public String getRange() {
		return "filename";
	}
}
