package io.sugo.pio.tools;

import java.net.URL;


/**
 * ResourceSources can be added to the {@link Tools} class in order to allow resource loading for
 * both the RapidMiner core and the plugin. Each plugin might add a new new resource source
 * indicating where the sources of the plugin can be found.
 * 
 * Please note that a resource path is only allowed to contain '/' instead of using File.separator.
 * This must be considered if a new prefix should be defined.
 * 
 * @author Ingo Mierswa
 */
public class ResourceSource {

	private ClassLoader loader;

	private String prefix;

	public ResourceSource(ClassLoader loader) {
		this(loader, Tools.RESOURCE_PREFIX);
	}

	public ResourceSource(ClassLoader loader, String prefix) {
		this.loader = loader;
		this.prefix = prefix;
		if (!prefix.endsWith("/")) {
			this.prefix = prefix + "/";
		}
	}

	public URL getResource(String name) {
		return loader.getResource((prefix + name).trim());
	}

	@Override
	public String toString() {
		return loader + "(" + prefix + ")";
	}
}
