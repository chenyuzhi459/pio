package io.sugo.pio.tools;

import java.util.*;

/**
 * @author Nils Woehler
 * 
 */
public class DominatingClassFinder<T> {
	/**
	 * @return Returns the next dominating or parent class found in the list of available classes.
	 */
	public Class<? extends T> findNextDominatingClass(Class<? extends T> clazz,
			Collection<Class<? extends T>> availableClasses) {

		Set<Class<? extends T>> candidates = new HashSet<Class<? extends T>>();

		// fetch candidates
		for (Class<? extends T> candidate : availableClasses) {
			if (candidate.isAssignableFrom(clazz)) {
				candidates.add(candidate);
			}
		}

		// iterate over candidates and extract dominated candidates
		boolean dominatedClassesFound = true;
		while (dominatedClassesFound) {
			dominatedClassesFound = false;
			List<Class<? extends T>> dominatedList = new LinkedList<Class<? extends T>>();

			// if more than one candidate is left...
			if (candidates.size() != 1) {

				// iterate over all candidate pairs and add all candidates that are dominated by
				// other candidates into a list
				for (Class<? extends T> candidate : candidates) {
					for (Class<? extends T> comperable : candidates) {
						if (candidate != comperable) {
							if (comperable.isAssignableFrom(candidate)) {
								dominatedList.add(candidate);
								dominatedClassesFound = true;
							}
						}
					}
				}
			}

			// if dominates classes have been found set them as new candidates for the next
			// iteration
			if (dominatedClassesFound) {
				candidates.clear();
				candidates.addAll(dominatedList);
			}
		}
		// this loop should break with only one candidate left, BUT: theoretically there can be more
		// than one

		// and select the first candidate found
		if (candidates.isEmpty()) {
			return null;
		} else {
			return candidates.iterator().next();
		}
	}

}
