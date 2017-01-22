package io.sugo.pio;


import io.sugo.pio.operator.Operator;

/**
 * Listens to events during the run of an process.
 * 
 * @author Ingo Mierswa Exp $
 */
public interface ProcessListener {

	/** Will be invoked during process start. */
	public void processStarts(Process process);

	/** Will be invoked every time another operator is started in the process. */
	public void processStartedOperator(Process process, Operator op);

	/** Will be invoked every time an operator is finished */
	public void processFinishedOperator(Process process, Operator op);

	/** Will invoked when the process was successfully finished. */
	public void processEnded(Process process);

}
