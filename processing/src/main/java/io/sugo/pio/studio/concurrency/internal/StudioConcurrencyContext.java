package io.sugo.pio.studio.concurrency.internal;


import io.sugo.pio.OperatorProcess;
import io.sugo.pio.core.concurrency.ConcurrencyContext;
import io.sugo.pio.core.concurrency.ExecutionStoppedException;
import io.sugo.pio.studio.internal.ProcessStoppedRuntimeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * Simple {@link ConcurrencyContext} to be used with a single {@link Process}.
 * <p>
 * The context does not implement the submission methods for {@link ForkJoinTask}s.
 *
 * @author Gisa Schaefer, Michael Knopf
 * @since 6.2.0
 */
public class StudioConcurrencyContext implements ConcurrencyContext {

	/**
	 * The current ForkJoinPool implementation restricts the maximum number of running threads to
	 * 32767. Attempts to create pools with greater than the maximum number result in
	 * IllegalArgumentException.
	 */
	private static final int FJPOOL_MAXIMAL_PARALLELISM = 32767;

	/** Locks to handle access from different threads */
	private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock(true);
	private static final Lock READ_LOCK = LOCK.readLock();
	private static final Lock WRITE_LOCK = LOCK.writeLock();

	/** The fork join pool all task are submitted to. */
	private static ForkJoinPool pool = null;

	/** The corresponding process. */
	private final OperatorProcess process;

	/**
	 * Creates a new {@link ConcurrencyContext} for the given {@link Process}.
	 * <p>
	 * The context assumes that only operators that belong to the corresponding process submit tasks
	 * to this context.
	 *
	 * @param process
	 *            the corresponding process
	 */
	public StudioConcurrencyContext(OperatorProcess process) {
		if (process == null) {
			throw new IllegalArgumentException("process must not be null");
		}
		this.process = process;
	}

	@Override
	public void run(List<Runnable> runnables) throws ExecutionException, ExecutionStoppedException {
		if (runnables == null) {
			throw new IllegalArgumentException("runnables must not be null");
		}

		// nothing to do if list is empty
		if (runnables.isEmpty()) {
			return;
		}

		// check for null runnables
		for (Runnable runnable : runnables) {
			if (runnable == null) {
				throw new IllegalArgumentException("runnables must not contain null");
			}
		}

		// wrap runnables in callables
		List<Callable<Void>> callables = new ArrayList<>(runnables.size());
		for (final Runnable runnable : runnables) {
			callables.add(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					runnable.run();
					return null;
				}
			});
		}

		// submit callables without further checks
		callUnchecked(callables);
	}

	@Override
	public <T> List<T> call(List<Callable<T>> callables) throws ExecutionException, ExecutionStoppedException {
		if (callables == null) {
			throw new IllegalArgumentException("callables must not be null");
		}

		// nothing to do if list is empty
		if (callables.isEmpty()) {
			return Collections.emptyList();
		}

		// check for null tasks
		for (Callable<T> callable : callables) {
			if (callable == null) {
				throw new IllegalArgumentException("callables must not contain null");
			}
		}

		// submit callables without further checks
		List<T> result = callUnchecked(callables);
		return result;
	}

	/**
	 * Utility method to submit a list of {@code Callable}s without further checks of the submission
	 * lock.
	 *
	 * @param callables
	 *            the callables
	 * @return the callables' results
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * @throws ExecutionStoppedException
	 *             if the computation was stopped
	 */
	private <T> List<T> callUnchecked(List<Callable<T>> callables) throws ExecutionException, ExecutionStoppedException {
		// the following line is blocking
		List<Future<T>> futures = getForkJoinPool().invokeAll(callables);
		List<T> results = new ArrayList<>(callables.size());
		for (Future<T> future : futures) {
			try {
				T result = future.get();
				results.add(result);
			} catch (InterruptedException | RejectedExecutionException e) {
				// The pool's invokeAll() method calls Future.get() internally. If the process is
				// stopped by the user, these calls might be interrupted before calls to
				// checkStatus() throw an ExecutionStoppedException. Thus, we need to check the
				// current status again.
				checkStatus();
				// InterruptedExceptions are very unlikely to happen at this point, since the above
				// calls to get() will return immediately. A RejectedExectutionException is an
				// extreme corner case as well. In both cases, there is no benefit for the API user
				// if the exception is passed on directly. Thus, we can wrap it within a
				// ExecutionException which is part of the API.
				throw new ExecutionException(e);
			} catch (ExecutionException e) {
				// A ProcessStoppedRuntimeException is an internal exception thrown if the user
				// requests the process to stop (see the checkStatus() implementation of this
				// class). This exception should not be wrapped or consumed here, since it is
				// handled by the operator implementation itself.
				if (e.getCause() instanceof ProcessStoppedRuntimeException) {
					throw (ExecutionStoppedException) e.getCause();
				} else {
					throw e;
				}
			}
		}

		return results;
	}

	@Override
	public int getParallelism() {
		READ_LOCK.lock();
		try {
			if (pool != null) {
				return pool.getParallelism();
			} else {
				return getDesiredParallelismLevel();
			}
		} finally {
			READ_LOCK.unlock();
		}
	}

	@Override
	public void checkStatus() throws ExecutionStoppedException {
//		if (process != null && process.shouldStop()) {
//			throw new ProcessStoppedRuntimeException();
//		}
	}

	@Override
	public <T> T invoke(ForkJoinTask<T> task) throws ExecutionException, ExecutionStoppedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> List<T> invokeAll(final List<ForkJoinTask<T>> tasks) throws ExecutionException, ExecutionStoppedException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Verifies and fetches the ForkJoinPool.
	 *
	 * @return the pool which should be used for execution.
	 */
	private static ForkJoinPool getForkJoinPool() {
		READ_LOCK.lock();
		try {
			if (!isPoolOutdated()) {
				// nothing to do
				return pool;
			}
		} finally {
			READ_LOCK.unlock();
		}
		WRITE_LOCK.lock();
		try {
			if (!isPoolOutdated()) {
				// pool has been updated in the meantime
				// no reason to re-create the pool once again
				return pool;
			}
			if (pool != null) {
				pool.shutdown();
			}
			pool = new ForkJoinPool(getDesiredParallelismLevel());
			return pool;
		} finally {
			WRITE_LOCK.unlock();
		}
	}

	/**
	 * Checks if the pool needs to be re-created.
	 *
	 * @return {@code true} if the current pool is {@code null} or the
	 *         {@link #getDesiredParallelismLevel()} is not equal to the current {@link #pool}
	 *         parallelism otherwise {@code false}
	 */
	private static boolean isPoolOutdated() {
		if (pool == null) {
			return true;
		}
		return getDesiredParallelismLevel() != pool.getParallelism();
	}

	/**
	 * Returns the desired number of cores to be used for concurrent computations. This number is
	 * always at least one and either bound by a license limit or by the user's configuration.
	 *
	 * @return the desired parallelism level
	 */
	private static int getDesiredParallelismLevel() {

		int userLevel = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);

		// should not happen, but we want to avoid any exception
		// during the pool creation
		if (userLevel > FJPOOL_MAXIMAL_PARALLELISM) {
			userLevel = FJPOOL_MAXIMAL_PARALLELISM;
		}
		return userLevel;
	}
}
