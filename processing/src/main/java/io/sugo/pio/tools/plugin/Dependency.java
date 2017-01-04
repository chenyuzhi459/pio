package io.sugo.pio.tools.plugin;

import io.sugo.pio.tools.ManagedExtension;

import java.util.*;


/**
 * A plugin dependency defines the name and version of a desired plugin.
 *
 * @author Ingo Mierswa
 */
public class Dependency {

	/** The name of the desired plugin. */
	private String extensionId;

	/** The version of the desired plugin. */
	private String version;

	/** Create a new plugin dependency. */
	public Dependency(String name, String version) {
		this.extensionId = name;
		this.version = version;
	}

	/**
	 * Returns true if the set contains a extension with the desired name and version.
	 */
	public boolean isFulfilled(Collection<Plugin> plugins) {
		Iterator<Plugin> i = plugins.iterator();
		while (i.hasNext()) {
			Plugin plugin = i.next();
			if (plugin.getExtensionId().equals(this.extensionId) && ManagedExtension.normalizeVersion(plugin.getVersion())
					.compareTo(ManagedExtension.normalizeVersion(this.version)) >= 0) {
				return true;
			}
		}
		return false;
	}

	public String getPluginExtensionId() {
		return extensionId;
	}

	public String getPluginVersion() {
		return version;
	}

	@Override
	public String toString() {
		return extensionId + " (" + version + ")";
	}

	public static List<Dependency> parse(String dependencyString) {
		if (dependencyString == null || dependencyString.isEmpty()) {
			return Collections.emptyList();
		}
		List<Dependency> result = new LinkedList<Dependency>();
		String[] singleDependencies = dependencyString.trim().split(";");
		for (int i = 0; i < singleDependencies.length; i++) {
			if (singleDependencies[i].trim().length() > 0) {
				String dependencyName = singleDependencies[i].trim();
				String dependencyVersion = "0";
				if (singleDependencies[i].trim().indexOf("[") >= 0) {
					dependencyName = singleDependencies[i].trim().substring(0, singleDependencies[i].trim().indexOf("["))
							.trim();
					dependencyVersion = singleDependencies[i].trim().substring(singleDependencies[i].trim().indexOf("[") + 1,
							singleDependencies[i].trim().indexOf("]")).trim();
				}
				result.add(new Dependency(dependencyName, dependencyVersion));
			}
		}
		return result;
	}
}
