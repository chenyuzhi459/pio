package io.sugo.pio.tools;

import java.io.File;
import java.io.IOException;


/**
 * This service offers methods for accessing the file system. For example to get the current
 * RapidMiner directory, used home directory and several else.
 *
 * @author Sebastian Land
 */
public class FileSystemService {

	/** folder in which extensions have their workspace */
	private static final String RAPIDMINER_EXTENSIONS_FOLDER = "extensions";
	/** folder in which extensions get their own folder to work with files */
	private static final String RAPIDMINER_EXTENSIONS_WORKSPACE_FOLDER = "workspace";
	/** folder which can be used to share data between extensions */
	private static final String RAPIDMINER_SHARED_DATA = "shared data";

	public static final String RAPIDMINER_USER_FOLDER = ".RapidMiner";
	public static final String RAPIDMINER_CONFIG_FILE_NAME = "rapidminer-studio-settings.cfg";
	public static final String PROPERTY_RAPIDMINER_SRC_ROOT = "rapidminer.src.root";

	/** Returns the main user configuration file. */
	public static File getMainUserConfigFile() {
		return FileSystemService.getUserConfigFile(RAPIDMINER_CONFIG_FILE_NAME);
	}

	/** Returns the memory configuration file containing the max memory. */
	public static File getMemoryConfigFile() {
		return new File(getUserRapidMinerDir(), "memory");
	}

	/** Returns the RapidMiner log file. */
	public static File getLogFile() {
		return new File(getUserRapidMinerDir(), "rapidminer-studio.log");
	}

	/**
	 * Returns the configuration file in the user dir {@link #RAPIDMINER_USER_FOLDER}.
	 */
	public static File getUserConfigFile(String name) {
		String configName = name;
		return new File(getUserRapidMinerDir(), configName);
	}

	public static File getUserRapidMinerDir() {
		File homeDir = new File(System.getProperty("user.home"));
		File userHomeDir = new File(homeDir, RAPIDMINER_USER_FOLDER);
		File extensionsWorkspaceRootFolder = new File(userHomeDir, RAPIDMINER_EXTENSIONS_FOLDER);
		File extensionsWorkspaceFolder = new File(extensionsWorkspaceRootFolder, RAPIDMINER_EXTENSIONS_WORKSPACE_FOLDER);
		File sharedDataDir = new File(userHomeDir, RAPIDMINER_SHARED_DATA);

		if (!userHomeDir.exists()) {
			boolean result = userHomeDir.mkdir();
		}
		if (!extensionsWorkspaceRootFolder.exists()) {
			boolean result = extensionsWorkspaceRootFolder.mkdir();
		}
		if (!extensionsWorkspaceFolder.exists()) {
			boolean result = extensionsWorkspaceFolder.mkdir();
		}
		if (!sharedDataDir.exists()) {
			boolean result = sharedDataDir.mkdir();
		}
		return userHomeDir;
	}

	/**
	 * Returns the folder for which an extension has read/write/delete permissions. The folder is
	 * located in the {@link #getUserRapidMinerDir()} folder.
	 *
	 * @param extensionId
	 *            the key of the extension, e.g. {@code rmx_myextension}
	 * @return a file with the working directory for the given extension id, never {@code null}
	 */
	public static File getPluginRapidMinerDir(String extensionId) {
		File userHomeDir = getUserRapidMinerDir();
		File extensionFolder = new File(userHomeDir, "extensions/workspace/" + extensionId);
		if (!extensionFolder.exists()) {
			extensionFolder.mkdir();
		}

		return extensionFolder;
	}

	public static File getRapidMinerHome() throws IOException {
		String property = System.getProperty(PlatformUtilities.PROPERTY_RAPIDMINER_HOME);
		if (property == null) {
			throw new IOException("Property " + PlatformUtilities.PROPERTY_RAPIDMINER_HOME + " is not set");
		}
		// remove any line breaks that snuck in for some reason
		property = property.replaceAll("\\r|\\n", "");
		return new File(property);
	}

	public static File getLibraryFile(String name) throws IOException {
		File home = getRapidMinerHome();
		return new File(home, "lib" + File.separator + name);
	}

	public static File getSourceRoot() {
		String srcName = System.getProperty(PROPERTY_RAPIDMINER_SRC_ROOT);
		if (srcName == null) {
			return null;
		} else {
			return new File(srcName);
		}
	}

	public static File getSourceFile(String name) {
		File root = getSourceRoot();
		if (root == null) {
			return null;
		} else {
			return new File(new File(root, "src"), name);
		}
	}

	public static File getSourceResourceFile(String name) {
		File root = getSourceRoot();
		if (root == null) {
			return null;
		} else {
			return new File(new File(root, "resources"), name);
		}
	}

}
