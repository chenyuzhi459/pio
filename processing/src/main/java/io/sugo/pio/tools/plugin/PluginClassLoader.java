package io.sugo.pio.tools.plugin;

import io.sugo.pio.operator.Operator;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * The class loader for a plugin (extending URLClassLoader). Since a plugin might depend on other
 * plugins the URLs of these plugins are also added to the current class loader.
 *
 * @author Ingo Mierswa, Michael Knopf
 */
public class PluginClassLoader extends URLClassLoader {

	private ArrayList<Plugin> dependencies = new ArrayList<>();

	private String pluginKey = null;

	private volatile boolean ignoreDependencyClassloaders;

	/**
	 * @return {@code true} if the dependency classloaders are ignored by
	 *         {@link #getResource(String)} and {@link #loadClass(String)}
	 */
	public boolean isIgnoreDependencyClassloaders() {
		return ignoreDependencyClassloaders;
	}

	/**
	 * Specifies if the dependency classloaders are ignored by {@link #getResource(String)} and
	 * {@link #loadClass(String)}.
	 *
	 * @param ignoreDependencyClassloaders
	 *            flag to ignore dependency classloaders when looking for resources or loading
	 *            classes
	 */
	public void setIgnoreDependencyClassloaders(boolean ignoreDependencyClassloaders) {
		this.ignoreDependencyClassloaders = ignoreDependencyClassloaders;
	}

	/**
	 * This constructor is for plugins that only depend on the core.
	 *
	 * @param urls
	 *            These URLs will be used for class building.
	 */
	public PluginClassLoader(URL[] urls) {
		super(urls, Operator.class.getClassLoader());
	}

	@Deprecated
	public PluginClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	/**
	 * This method can be used if for a plugin already is known which parent plugins are needed.
	 * Otherwise you can use the standard constructor and add the Dependencies later using
	 * {@link #addDependency(Plugin)}.
	 *
	 * @param urls
	 * @param parentPlugins
	 */
	public PluginClassLoader(URL[] urls, Plugin... parentPlugins) {
		super(urls, Operator.class.getClassLoader());

		for (Plugin plugin : parentPlugins) {
			this.dependencies.add(plugin);
		}
	}

	/**
	 * Adds a plugin to the list of dependencies.
	 *
	 * @param dependency
	 *            The new dependency.
	 */
	public void addDependency(Plugin dependency) {
		dependencies.add(dependency);
	}

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class clazz = null;

		try {
			clazz = super.loadClass(name, resolve);
		} catch (ClassNotFoundException e) {
			// ClassNotFoundException thrown if class not found
			// from the urls registered nor the core class loader
		}
		// look into dependency classloaders if not found and we are allowed to do so
		if (clazz == null && !ignoreDependencyClassloaders) {
			for (Plugin plugin : dependencies) {
				try {
					return plugin.getClassLoader().loadClass(name, resolve);
				} catch (ClassNotFoundException e) {
					// ClassNotFoundException thrown if class not found
					// from the parent extension
				}
			}
		}
		if (clazz == null) {
			// If still not found, then invoke findClass in order
			// to find the class.
			clazz = findClass(name);
		}
		// if no class found during findClass an Exception is thrown anyway
		if (resolve) {
			resolveClass(clazz);
		}
		return clazz;
	}

	@Override
	public URL getResource(String name) {
		URL url = super.getResource(name);

		// look into dependency classloaders if not found and we are allowed to do so
		if (!ignoreDependencyClassloaders) {
			for (Plugin dependency : dependencies) {
				url = dependency.getClassLoader().getResource(name);
				if (url != null) {
					break;
				}
			}
		}

		if (url == null) {
			url = findResource(name);
		}
		return url;
	}

	@Override
	public String toString() {
		return "PluginClassLoader (" + Arrays.asList(getURLs()) + ")";
	}

	/**
	 * Returns the key of the plugin for this classloader. Can be {@code null} if it has not been
	 * specified.
	 *
	 * @return the plugin key or {@code null}
	 */
	public String getPluginKey() {
		return pluginKey;
	}

	/**
	 * Set the key of the plugin for this classloader.
	 *
	 * @param pluginKey
	 *            the key
	 * @throws SecurityException
	 *             if caller does not have {@link RuntimePermission} for {@code createClassLoader}
	 */
	public void setPluginKey(String pluginKey) {
		if (System.getSecurityManager() != null) {
			AccessController.checkPermission(new RuntimePermission("createClassLoader"));
		}
		this.pluginKey = pluginKey;
	}

	/**
	 * Returns a {@link Set} of {@link PluginClassLoader}s this {@link PluginClassLoader} depends on
	 * (i.e., the corresponding {@link Plugin} depends on):
	 *
	 * @return Set of dependency {@link PluginClassLoader}s.
	 */
	public Set<PluginClassLoader> getDependencyClassLoaders() {
		Set<PluginClassLoader> classLoaders = new HashSet<>();
		// add class loaders of dependencies
		for (Plugin dependency : dependencies) {
			@SuppressWarnings("resource")
			PluginClassLoader dependencyClassLoader = dependency.getClassLoader();
			// add the dependency itself
			classLoaders.add(dependencyClassLoader);
			// add the dependency's dependencies
			classLoaders.addAll(dependencyClassLoader.getDependencyClassLoaders());
		}
		return classLoaders;
	}
}
