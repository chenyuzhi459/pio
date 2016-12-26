package io.sugo.pio.operator.io;


import io.sugo.pio.operator.Operator;

/**
 * This is the interface for the factories of {@link DataResultSet}s. They usually store information
 * needed to create the result set like for example resource identifiers, queries and so on. From
 * that, they construct the result set.
 *
 * @author Sebastian Land, Simon Fischer
 */
public interface DataResultSetFactory extends AutoCloseable {

	/**
	 * Returns the human readable name of the resource read (most often, this will be a file or
	 * URL).
	 */
	String getResourceName();

	/** Sets the configuration parameters in the given reader operator. */
	void setParameters(AbstractDataResultSetReader reader);

	/** Closes all resources associated with this factory without throwing an exception. */
	@Override
	void close();

	/** Creates a result set. Make sure to call {@link #close()} after using this method. */
	public DataResultSet makeDataResultSet(Operator operator);
}
