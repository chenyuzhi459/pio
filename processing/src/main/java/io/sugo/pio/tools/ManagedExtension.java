package io.sugo.pio.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarFile;


/**
 *
 * @author Simon Fischer
 *
 */
public class ManagedExtension {

	/** Maps {@link ManagedExtension#getPackageId()} to the ManagedExtension itself. */
	private static final Map<String, ManagedExtension> MANAGED_EXTENSIONS = new TreeMap<>((ext1, ext2) -> {
		if (ext1 == null && ext2 == null) {
			return 0;
		}

		if (ext1 == null) {
			return 1;
		}

		if (ext2 == null) {
			return -1;
		}
		return ext1.compareTo(ext2);
	});

	private final SortedSet<String> installedVersions = new TreeSet<>(new Comparator<String>() {

		@Override
		public int compare(String o1, String o2) {
			return normalizeVersion(o1).compareTo(normalizeVersion(o2));
		}
	});
	private final String packageID;
	private final String name;
	private String selectedVersion;
	private boolean active;
	private final String license;

	private ManagedExtension(String id, String name, String license) {
		super();
		this.packageID = id;
		this.name = name;
		this.license = license;
		this.selectedVersion = null;
		this.setActive(true);
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public String getName() {
		return name;
	}

	private File findFile() {
		return findFile(selectedVersion);
	}

	private File findFile(String version) {
		File file = new File(getUserExtensionsDir(), packageID + "-" + version + ".jar");
		if (file.exists()) {
			return file;
		}
		return null;
	}

	public static File getUserExtensionsDir() {
		return FileSystemService.getUserConfigFile("managed");
	}

	/**
	 * This method returns the jar file of the extension or throws an {@link FileNotFoundException}
	 * exception.
	 */
	public JarFile findArchive() throws IOException {
		File findFile = findFile();
		if (findFile != null) {
			return new JarFile(findFile);
		}
		throw new FileNotFoundException("Could not access file of installed extension.");
	}

	public JarFile findArchive(String version) throws IOException {
		File findFile = findFile(version);
		if (findFile == null) {
			throw new IOException(
					"Failed to find extension jar file (extension " + getName() + ", version " + version + ").");
		} else {
			try {
				return new JarFile(findFile);
			} catch (IOException e) {
				throw new IOException("Failed to open jar file " + findFile + ": " + e, e);
			}
		}
	}


	public static List<File> getActivePluginJars() {
		List<File> result = new LinkedList<>();
		for (ManagedExtension ext : MANAGED_EXTENSIONS.values()) {
			if (ext.isActive()) {
				File file = ext.findFile();
				if (file != null) {
					result.add(file);
				}
			}
		}
		return result;
	}

	public static ManagedExtension get(String packageId) {
		return MANAGED_EXTENSIONS.get(packageId);
	}

	public static ManagedExtension remove(String packageId) {
		return MANAGED_EXTENSIONS.remove(packageId);
	}

	public String getPackageId() {
		return packageID;
	}

	public static Collection<ManagedExtension> getAll() {
		return MANAGED_EXTENSIONS.values();
	}

	/**
	 * Adds leading zeroes until the version is of the form {@code xx.yy.zzz}.
	 */
	public static String normalizeVersion(String version) {
		if (version == null) {
			return null;
		}
		String[] split = version.split("\\.");
		if (split.length < 3) {
			String[] newSplit = new String[3];
			System.arraycopy(split, 0, newSplit, 0, split.length);
			for (int i = split.length; i < newSplit.length; i++) {
				newSplit[i] = "0";
			}
			split = newSplit;
		}
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < split.length; i++) {
			int lastDigit;
			for (lastDigit = split[i].length() - 1; lastDigit >= 0; lastDigit--) {
				if (Character.isDigit(split[i].charAt(lastDigit))) {
					break;
				}
			}
			String letters = split[i].substring(lastDigit + 1);
			String digits = split[i].substring(0, lastDigit + 1);
			int desiredLength = i == split.length - 1 ? 3 : 2;
			while (digits.length() < desiredLength) {
				digits = "0" + digits;
			}
			if (i != 0) {
				result.append('.');
			}
			result.append(digits).append(letters);
		}
		return result.toString();
	}
}
