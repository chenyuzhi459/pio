package io.sugo.pio.repository;


import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.ports.metadata.MetaData;
import io.sugo.pio.tools.ProgressListener;

/**
 * @author Simon Fischer
 */
public interface IOObjectEntry extends DataEntry {

	public static final String TYPE_NAME = "data";

	public IOObject retrieveData(ProgressListener l) throws RepositoryException;

	public MetaData retrieveMetaData() throws RepositoryException;

	/**
	 * This method returns the class of the stored object or null, if it is not an object known to
	 * this RapidMiner Client.
	 */
	public Class<? extends IOObject> getObjectClass();

	/** Stores data in this entry. */
	public void storeData(IOObject data, Operator callingOperator, ProgressListener l) throws RepositoryException;

}
