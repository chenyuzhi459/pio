package io.sugo.pio.repository;


import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.tools.ProgressListener;

import java.util.List;


/**
 * An entry containing sub-entries.
 * 
 *
 */
public interface Folder extends Entry {

	public static final String TYPE_NAME = "folder";

	public List<DataEntry> getDataEntries() throws RepositoryException;

	public List<Folder> getSubfolders() throws RepositoryException;

	public void refresh() throws RepositoryException;

	public boolean containsEntry(String name) throws RepositoryException;

	public Folder createFolder(String name) throws RepositoryException;

	public IOObjectEntry createIOObjectEntry(String name, IOObject ioobject, Operator callingOperator,
											 ProgressListener progressListener) throws RepositoryException;

	public ProcessEntry createProcessEntry(String name, String processXML) throws RepositoryException;

	public BlobEntry createBlobEntry(String name) throws RepositoryException;

	/**
	 * Returns true iff a child with the given name exists and a {@link #refresh()} would find this
	 * entry (or it is already loaded).
	 * 
	 * @throws RepositoryException
	 */
	public boolean canRefreshChild(String childName) throws RepositoryException;
}
