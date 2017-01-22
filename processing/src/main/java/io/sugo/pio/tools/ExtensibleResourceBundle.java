package io.sugo.pio.tools;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * This class represents an extensible resource bundle, where other resource bundles might be added.
 * These are searched if the key isn't contained in the previously added bundles.
 * 
 * @author Sebastian Land
 */
public class ExtensibleResourceBundle extends ResourceBundle {

	private class KeyEnumeration implements Enumeration<String> {

		private Iterator<ResourceBundle> bundleIterator = bundles.iterator();
		private Enumeration<String> keyIterator;

		@Override
		public boolean hasMoreElements() {
			if (keyIterator == null || !keyIterator.hasMoreElements()) {
				while (bundleIterator.hasNext() && (keyIterator == null || !keyIterator.hasMoreElements())) {
					keyIterator = bundleIterator.next().getKeys();
				}
			}
			if (keyIterator == null || !keyIterator.hasMoreElements()) {
				return false;
			}
			return true;
		}

		@Override
		public String nextElement() {
			if (keyIterator == null) {
				keyIterator = bundleIterator.next().getKeys();
			}
			while (!keyIterator.hasMoreElements()) {
				keyIterator = bundleIterator.next().getKeys();
			}
			return keyIterator.nextElement();
		}
	}

	private List<ResourceBundle> bundles = new CopyOnWriteArrayList<>();

	public ExtensibleResourceBundle(ResourceBundle parent) {
		bundles.add(parent);
	}

	/**
	 * This method extends this resource bundle with the properties set by the given bundle. If
	 * those properties are already contained, they will be ignored.
	 */
	public void addResourceBundle(ResourceBundle bundle) {
		bundles.add(bundle);
	}

	/**
	 * This method extends this resource bundle with the properties set by the given bundle. If
	 * those properties were already contained, they are overwritten by the new bundle's settings.
	 */
	public void addResourceBundleAndOverwrite(ResourceBundle bundle) {
		bundles.add(0, bundle);
	}

	@Override
	public Enumeration<String> getKeys() {
		return new KeyEnumeration();
	}

	@Override
	protected Object handleGetObject(String key) {
		for (ResourceBundle bundle : bundles) {
			if (bundle.containsKey(key)) {
				return bundle.getObject(key);
			}
		}
		return null;
	}

	@Override
	public boolean containsKey(String key) {
		if (super.containsKey(key)) {
			return true;
		}

		for (ResourceBundle subbundle : bundles) {
			if (subbundle.containsKey(key)) {
				return true;
			}
		}
		return false;
	}
}
