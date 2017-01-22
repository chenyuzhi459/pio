package io.sugo.pio.parameter;

/**
 * A parameter type for specifying a repository location.
 * 
 */
public class ParameterTypeRepositoryLocation extends ParameterTypeString {

	private static final long serialVersionUID = 1L;

	private boolean allowFolders, allowEntries, allowAbsoluteEntries, enforceValidRepositoryEntryName,
			onlyWriteableLocations;

	/**
	 * Creates a new parameter type for files with the given extension. If the extension is null no
	 * file filters will be used.
	 */
	public ParameterTypeRepositoryLocation(String key, String description, boolean optional) {
		this(key, description, true, false, optional);
	}

	/**
	 * Creates a new parameter type for files with the given extension. If the extension is null no
	 * file filters will be used.
	 */
	public ParameterTypeRepositoryLocation(String key, String description, boolean allowEntries, boolean allowDirectories,
										   boolean optional) {
		this(key, description, allowEntries, allowDirectories, false, optional, false, false);
	}

	public ParameterTypeRepositoryLocation(String key, String description, boolean allowEntries, boolean allowDirectories,
										   boolean allowAbsoluteEntries, boolean optional, boolean enforceValidRepositoryEntryName) {
		this(key, description, allowEntries, allowDirectories, allowAbsoluteEntries, optional,
				enforceValidRepositoryEntryName, false);
	}

	/**
	 * Creates a new parameter type for files with the given extension. If the extension is null no
	 * file filters will be used. If {@link #enforceValidRepositoryEntryName} is set to
	 * <code>true</code>, will enforce valid repository entry names.
	 **/
	public ParameterTypeRepositoryLocation(String key, String description, boolean allowEntries, boolean allowDirectories,
										   boolean allowAbsoluteEntries, boolean optional, boolean enforceValidRepositoryEntryName,
										   boolean onlyWriteableLocations) {
		super(key, description, null);

		setOptional(optional);
		setAllowEntries(allowEntries);
		setAllowFolders(allowDirectories);
		setAllowAbsoluteEntries(allowAbsoluteEntries);
		setEnforceValidRepositoryEntryName(enforceValidRepositoryEntryName);
		setOnlyWriteableLocations(onlyWriteableLocations);
	}

	/**
	 * Creates a new parameter type for files with the given extension. If the extension is null no
	 * file filters will be used.
	 */
	public ParameterTypeRepositoryLocation(String key, String description, boolean allowEntries, boolean allowDirectories,
										   boolean allowAbsoluteEntries, boolean optional) {
		this(key, description, allowEntries, allowDirectories, allowAbsoluteEntries, optional, false, false);
	}

	public boolean isOnlyWriteableLocations() {
		return onlyWriteableLocations;
	}

	public void setOnlyWriteableLocations(boolean onlyWriteableLocations) {
		this.onlyWriteableLocations = onlyWriteableLocations;
	}

	public boolean isAllowFolders() {
		return allowFolders;
	}

	public void setAllowFolders(boolean allowFolders) {
		this.allowFolders = allowFolders;
	}

	public boolean isAllowEntries() {
		return allowEntries;
	}

	public void setAllowEntries(boolean allowEntries) {
		this.allowEntries = allowEntries;
	}

	public void setAllowAbsoluteEntries(boolean allowAbsoluteEntries) {
		this.allowAbsoluteEntries = allowAbsoluteEntries;
	}

	public boolean isAllowAbsoluteEntries() {
		return this.allowAbsoluteEntries;
	}

	public boolean isEnforceValidRepositoryEntryName() {
		return enforceValidRepositoryEntryName;
	}

	public void setEnforceValidRepositoryEntryName(boolean enforceValidRepositoryEntryName) {
		this.enforceValidRepositoryEntryName = enforceValidRepositoryEntryName;
	}
}
