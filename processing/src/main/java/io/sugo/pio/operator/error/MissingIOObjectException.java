package io.sugo.pio.operator.error;

import io.sugo.pio.operator.UserError;

/**
 * Will be thrown if an operator can not get its desired input. Is usually thrown during a process
 * which was started although the validation delivered an message that the operator needs additional
 * input.
 * 
 * @author Ingo Mierswa, Simon Fischer ingomierswa Exp $
 */
public class MissingIOObjectException extends UserError {

	private static final long serialVersionUID = -4992990462748190926L;

	private Class wanted;

	public MissingIOObjectException(Class cls) {
		super(null, "pio.error.operator.miss_input_type", cls.getName());
		wanted = cls;
	}

	public Class getMissingClass() {
		return wanted;
	}
}
