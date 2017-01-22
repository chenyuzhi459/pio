package io.sugo.pio;

/**
 * This listener can be used to register for process state changes like start/pause/stop initiated
 * by user.
 * 
 *
 */
public interface ProcessStateListener {

	/**
	 * Fired if a process is started.
	 * 
	 * @param process
	 *            the process that has been started
	 */
	public void started(OperatorProcess process);

	/**
	 * Fired if a process was paused.
	 * 
	 * @param process
	 *            the process that was paused
	 */
	public void paused(OperatorProcess process);

	/**
	 * Fired if a process was resumed.
	 * 
	 * @param process
	 *            the process that was resumed
	 */
	public void resumed(OperatorProcess process);

	/**
	 * Fired if a process was stopped.
	 * 
	 * @param process
	 *            the process that was stopped
	 */
	public void stopped(OperatorProcess process);

}
