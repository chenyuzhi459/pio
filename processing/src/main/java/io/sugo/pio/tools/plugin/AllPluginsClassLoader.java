package io.sugo.pio.tools.plugin;

import io.sugo.pio.operator.Operator;
import io.sugo.pio.tools.plugin.Plugin;

import java.net.URL;


/**
 * A class loader that consecutively tries to load classes from all registered plugins. It starts
 * with the system class loader and then tries all plugins in the order as returned by
 * {@link Plugin#getAllPlugins()}.
 * 
 * @author Simon Fischer
 * 
 */
public class AllPluginsClassLoader extends ClassLoader {

	public AllPluginsClassLoader() {
		super(Operator.class.getClassLoader());
	}

	@Override
	public Class<? extends Object> loadClass(String name) throws ClassNotFoundException {
		for (Plugin plugin : Plugin.getAllPlugins()) {
			ClassLoader classLoader = plugin.getClassLoader();
			try {
				return classLoader.loadClass(name);
			} catch (ClassNotFoundException notFound) {
			}
		}
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		if ((contextClassLoader != null) && (contextClassLoader != this)) { // avoid stack overflow
			return contextClassLoader.loadClass(name);
		} else {
			throw new ClassNotFoundException(name);
		}
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		return loadClass(name);
	}

	/**
	 * Loads the resource from the first plugin classloader which does not return <code>null</code>
	 * and <code>null</code> if no classloader can find the resource.
	 */
	@Override
	public URL getResource(String name) {
		URL url = super.getResource(name);
		if (url != null) {
			return url;
		}
		for (Plugin plugin : Plugin.getAllPlugins()) {
			ClassLoader classLoader = plugin.getClassLoader();
			url = classLoader.getResource(name);
			if (url != null) {
				return url;
			}
		}
		return url;
	}

}
