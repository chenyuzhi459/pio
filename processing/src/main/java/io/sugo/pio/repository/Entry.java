package io.sugo.pio.repository;

import javax.swing.*;
import java.util.Collection;


/**
 * An entry in a repository. Can be either a folder or a data entry (leaf). This is either a view on
 * the local file system or a view on a remote repository.
 *
 * @author Simon Fischer, Nils Woehler
 *
 */
public interface Entry {

	/** Returns the name, the last part of the location. */
	public String getName();

	/** Returns a string describing the type: "folder", "data", "blob", or "process". */
	public String getType();

	/**
	 * Returns the user name of the owner. Returns <code>null</code> in case no owner has been
	 * specified.
	 */
	public String getOwner();

	/** Returns a human readable description. */
	public String getDescription();

	/** Returns true if this entry cannot be written to. */
	public boolean isReadOnly();

	/**
	 * Changes the name of the entry. The entry stays in the same folder.
	 *
	 * @throws RepositoryException
	 */
	public boolean rename(String newName) throws RepositoryException;

	/**
	 * Needs to be implemented only for folders in the same repository. Moving between different
	 * repositories is implemented by
	 * {@link RepositoryManager#move(RepositoryLocation, Folder, io.sugo.pio.tools.ProgressListener)}
	 * using a sequence of copy and delete.
	 *
	 * @throws RepositoryException
	 */
	public boolean move(Folder newParent) throws RepositoryException;

	/**
	 * Needs to be implemented only for folders in the same repository. Moving between different
	 * repositories is implemented by
	 * {@link RepositoryManager#move(RepositoryLocation, Folder, io.sugo.pio.tools.ProgressListener)}
	 * using a sequence of copy and delete.
	 *
	 * @param newName
	 *            New name for moved entry. If moved entry shouldn't be renamed: newName=null.
	 * @throws RepositoryException
	 */
	public boolean move(Folder newParent, String newName) throws RepositoryException;

	/** Returns the folder containing this entry. */
	public Folder getContainingFolder();

	/**
	 * Subclasses can use this method to signal whether getting information from this entry will
	 * block the current thread, e.g. because information must be fetched over the network.
	 */
	public boolean willBlock();

	/**
	 * A location, that can be used, e.g. as a parameter in the {@link RepositorySource} or which
	 * can be used to locate the entry using {@link RepositoryManager#resolve(String)}.
	 */
	public RepositoryLocation getLocation();

	/**
	 * Deletes the entry and its contents from the repository.
	 *
	 * @throws RepositoryException
	 */
	public void delete() throws RepositoryException;

	/** Returns custom actions to be displayed in this entry's popup menu. */
	public Collection<Action> getCustomActions();
}
