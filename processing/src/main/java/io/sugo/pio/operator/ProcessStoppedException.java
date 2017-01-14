package io.sugo.pio.operator;

/**
 * Exception class whose instances are thrown by instances of the class 'Operator' or of one of its
 * subclasses.
 *
 * @author Ingo Mierswa
 */
public class ProcessStoppedException extends OperatorException {

	private static final long serialVersionUID = 8299515313202467747L;

	private transient final Operator op;
	private String opName;

	public ProcessStoppedException(Operator o) {
		super("Process stopped in " + o.getName());
		this.op = o;
		this.opName = op.getName();
	}

	public ProcessStoppedException() {
		super("Process stopped");
		this.op = null;
		this.opName = "";
	}

	public Operator getOperator() {
		return op;
	}

	public String getOperatorName() {
		return opName;
	}

}
