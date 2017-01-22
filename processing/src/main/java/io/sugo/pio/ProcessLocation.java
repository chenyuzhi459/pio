package io.sugo.pio;


import io.sugo.pio.tools.ProgressListener;

import java.io.IOException;


/**
 * A place where a process can be saved. Basically, this is either a file or a location in a
 * repository.
 * 
 * @author Simon Fischer
 * */
public interface ProcessLocation {

	/** Reads the process and returns it. */
	public OperatorProcess load(ProgressListener listener) throws IOException;

	/** Stores the process at the referenced location. */
//	public void store(OperatorProcess process, ProgressListener listener) throws IOException;

	/** The toString representation is used, e.g. in the welcome screen dialog, */
	@Override
	public String toString();

	/** Returns a string saved to the history file. */
	public String toHistoryFileString();

	/** Returns a string as it is displayed in the recent files menu. */
	public String toMenuString();

	/** Returns a short name, e.g. the last component of the path. */
	public String getShortName();
}
