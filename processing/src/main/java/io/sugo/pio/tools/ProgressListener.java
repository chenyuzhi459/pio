package io.sugo.pio.tools;

/**
 * Interface for time consuming tasks.
 * 
 */
public interface ProgressListener {

	/** Sets the total amount of work to do, on an arbitrary scale. */
	public void setTotal(int total);

	/** Sets the amount of work completed, in the range [0, {@link #getTotal()}. */
	public void setCompleted(int completed);

	/** Notifies the listener that the task is complete. */
	public void complete();

	/** An optional message to display to the user. */
	public void setMessage(String message);
}
