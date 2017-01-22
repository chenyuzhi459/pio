package io.sugo.pio.repository;



/**
 * An entry that can store processes.
 * 
 *
 */
public interface ProcessEntry extends DataEntry {

	public static final String TYPE_NAME = "process";

	public String retrieveXML() throws RepositoryException;

	public void storeXML(String xml) throws RepositoryException;

}
