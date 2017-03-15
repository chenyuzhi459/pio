package io.sugo.pio.operator.nio.model;


import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.tools.ProgressListener;

import javax.swing.table.TableModel;

/**
 * This is the interface for the factories of {@link DataResultSet}s. They usually store information
 * needed to create the result set like for example resource identifiers, queries and so on. From
 * that, they construct the result set.
 *
 */
public interface DataResultSetFactory extends AutoCloseable {

	/** Creates a result set. Make sure to call {@link #close()} after using this method. */
	public DataResultSet makeDataResultSet(Operator operator) throws OperatorException;

	/**
	 * This method has to return a table model that can be used for showing a preview.
	 */
//	public TableModel makePreviewTableModel(ProgressListener listener) throws OperatorException, ParseException;

	/**
	 * Returns the human readable name of the resource read (most often, this will be a file or
	 * URL).
	 */
	public String getResourceName();

	/**
	 * Makes initial meta data. Only the number of rows should be filled in here. All other
	 * information will later be added by {@link DataResultSetTranslationConfiguration}
	 */
	public ExampleSetMetaData makeMetaData();

	/** Sets the configuration parameters in the given reader operator. */
	public void setParameters(AbstractDataResultSetReader reader);

	/** Closes all resources associated with this factory without throwing an exception. */
	@Override
	public void close();
}
