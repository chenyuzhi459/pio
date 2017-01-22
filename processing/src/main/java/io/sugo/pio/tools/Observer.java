package io.sugo.pio.tools;

/**
 * @see Observable
 *
 */
public interface Observer<A> {

	public void update(Observable<A> observable, A arg);

}
