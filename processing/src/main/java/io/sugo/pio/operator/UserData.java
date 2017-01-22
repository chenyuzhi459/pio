package io.sugo.pio.operator;

/**
 * Generic interface for user data objects.
 * 
 * @param <T>
 *            The type the user data will be attached to. An instance of this type will be supplied
 *            to the copy method.
 * @author Michael Knopf
 */
public interface UserData<T> {

	/**
	 * Invoked if the parent object is duplicated. Implementations may return self references,
	 * shallow copies, or deep copies, depending on what is appropriate in the context.
	 *
	 * @param newParent
	 *            the reference to the new (duplicated) parent object
	 * @return A self reference, a shallow copy, or a deep copy of the user data.
	 */
	public UserData<T> copyUserData(T newParent);
}
