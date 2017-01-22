package io.sugo.pio.repository;

/**
 * Anything that is not a folder.
 * 
 */
public interface DataEntry extends Entry {

	/** Returns the revision number of this entry. */
	public int getRevision();

	/** Returns the size of this entry in bytes. */
	public long getSize();

	/** Returns the last modification date of this entry. */
	public long getDate();

}
