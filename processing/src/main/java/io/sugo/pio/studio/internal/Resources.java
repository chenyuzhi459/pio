package io.sugo.pio.studio.internal;


import io.sugo.pio.OperatorProcess;
import io.sugo.pio.core.concurrency.ConcurrencyContext;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.UserData;
import io.sugo.pio.studio.concurrency.internal.StudioConcurrencyContext;

/**
 * Provides utility methods to access feature from the new API that cannot be injected directly yet.
 * Note that this class is supposed to be removed once the injection is implemented.
 *
 * @since 6.2.0
 */
public class Resources {

	private static final String USER_DATA_KEY = "io.sugo.pio.core.concurrency.ContextUserData";

	/**
	 * Wrapper to store {@link ConcurrencyContext} within the root operator of a process.
	 *
	 * @author Michael Knopf
	 */
	private static class ContextUserData implements UserData<Object> {

		private final ConcurrencyContext context;

		private ContextUserData(ConcurrencyContext context) {
			this.context = context;
		}

		@Override
		public UserData<Object> copyUserData(Object newParent) {
			return this;
		}

		private ConcurrencyContext getContext() {
			return this.context;
		}

	}

	/**
	 * Provides a {@link ConcurrencyContext} for the given {@link Operator}.
	 *
	 * @param operator
	 *            the operator
	 * @return the context
	 */
	public static ConcurrencyContext getConcurrencyContext(Operator operator) {
		if (operator == null) {
			throw new IllegalArgumentException("operator must not be null");
		}
		Operator root = operator.getRoot();
		if (root.getUserData(USER_DATA_KEY) != null) {
			ContextUserData data = (ContextUserData) root.getUserData(USER_DATA_KEY);
			ConcurrencyContext context = data.getContext();
			return context;
		}
		OperatorProcess process = operator.getProcess();
		StudioConcurrencyContext context = new StudioConcurrencyContext(process);
		ContextUserData data = new ContextUserData(context);
		root.setUserData(USER_DATA_KEY, data);
		return context;
	}
}
