package io.sugo.pio.parameter;


import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorVersion;
import io.sugo.pio.ports.InputPort;

import java.util.concurrent.Callable;

/**
 * This attribute type supports the user by letting him define an expression with a user interface
 * known from calculators.
 *
 * For knowing attribute names before process execution a valid meta data transformation must be
 * performed. Otherwise the user might type in the name, instead of choosing.
 *
 * @author Ingo Mierswa
 */
public class ParameterTypeExpression extends ParameterTypeString {

	private static final long serialVersionUID = -1938925853519339382L;

	private static final String ATTRIBUTE_INPUT_PORT = "port-name";

	private transient InputPort inPort;

	private Callable<OperatorVersion> operatorVersion;

	/**
	 * A simple functional which allows to query the current operator compatibility level.
	 */
	public static final class OperatorVersionCallable implements Callable<OperatorVersion> {

		private final Operator op;

		/**
		 * Constructor for the {@link OperatorVersionCallable}.
		 *
		 * @param op
		 *            the operator. Must not be {@code null}
		 */
		public OperatorVersionCallable(Operator op) {
			if (op == null) {
				throw new IllegalArgumentException("Operator must not be null");
			}

			this.op = op;
		}

		@Override
		public OperatorVersion call() {
			return op.getCompatibilityLevel();
		}

	}

	/**
	 * This constructor will generate a ParameterType that does not use the {@link MetaData} of an
	 * associated {@link InputPort} to verify the expressions.
	 *
	 * @param key
	 * @param description
	 *
	 * @deprecated use {@link #ParameterTypeExpression(String, String, OperatorVersionCallable)}
	 *             instead
	 */
	@Deprecated
	public ParameterTypeExpression(final String key, String description) {
		this(key, description, null, false);
	}

	/**
	 * This constructor will generate a ParameterType that does not use the {@link MetaData} of an
	 * associated {@link InputPort} to verify the expressions.
	 *
	 * @param key
	 *            the parameter key
	 * @param description
	 *            the parameter description
	 * @param operatorVersion
	 *            a functional which allows to query the current operator version. Must not be
	 *            {@code null} and must not return null
	 */
	public ParameterTypeExpression(final String key, String description, OperatorVersionCallable operatorVersion) {
		this(key, description, null, false, operatorVersion);
	}

	public ParameterTypeExpression(final String key, String description, InputPort inPort) {
		this(key, description, inPort, false);
	}

	public ParameterTypeExpression(final String key, String description, InputPort inPort, boolean optional, boolean expert) {
		this(key, description, inPort, optional);
//		setExpert(expert);
	}

	public ParameterTypeExpression(final String key, String description, final InputPort inPort, boolean optional) {
		this(key, description, inPort, optional, new Callable<OperatorVersion>() {

			@Override
			public OperatorVersion call() throws Exception {
				if (inPort != null) {
					return inPort.getPorts().getOwner().getOperator().getCompatibilityLevel();
				} else {

					// callers that do not provide an input port are not be able to use the
					// expression parser functions
					return new OperatorVersion(6, 4, 0);
				}
			}
		});
		if (inPort == null) {
//			LogService.getRoot().log(Level.INFO, "io.sugo.pio.parameter.ParameterTypeExpression.no_input_port_provided");
		}
	}

	private ParameterTypeExpression(final String key, String description, InputPort inPort, boolean optional,
									Callable<OperatorVersion> operatorVersion) {
		super(key, description, optional);

		if (operatorVersion == null) {
			throw new IllegalArgumentException("Operator version parameter must not be null");
		}

		this.inPort = inPort;
		this.operatorVersion = operatorVersion;
	}

	@Override
	public Object getDefaultValue() {
		return "";
	}

	/**
	 * Returns the input port associated with this ParameterType. This might be null!
	 */
	public InputPort getInputPort() {
		return inPort;
	}

}
