package io.sugo.pio.studio.internal;


import io.sugo.pio.core.concurrency.ExecutionStoppedException;

/**
 * Unchecked variant of a {@link ProcessStoppedException}. May be thrown by
 * {@link ConcurrencyContext#checkStatus()} or other places, that only allow to throw unchecked
 * exceptions, if the corresponding process should stop.
 *
 * @author Michael Knopf
 * @since 6.2.0
 */
public class ProcessStoppedRuntimeException extends ExecutionStoppedException {

	private static final long serialVersionUID = 1L;

}
