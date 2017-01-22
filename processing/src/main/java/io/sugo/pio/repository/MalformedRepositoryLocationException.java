package io.sugo.pio.repository;

import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.UserError;

public class MalformedRepositoryLocationException extends OperatorException {

	private static final long serialVersionUID = 1L;

	public MalformedRepositoryLocationException(String message) {
		super(message);
	}

	public UserError makeUserError(Operator operator) {
		return new UserError(operator, this, 319, this.getMessage());
	}
}
