package io.sugo.pio.repository;

import java.util.EventListener;


/**
 * A listener listening to changes of a repository.
 * 
 */
public interface RepositoryListener extends EventListener {

	public void entryAdded(Entry newEntry, Folder parent);

	public void entryChanged(Entry entry);

	public void entryRemoved(Entry removedEntry, Folder parent, int oldIndex);

	public void folderRefreshed(Folder folder);

}
