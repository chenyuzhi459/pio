package io.sugo.pio.tools;

import java.util.NoSuchElementException;


/**
 * Reimplementation of the Java 1.0 Observer pattern. It has some advantages:
 * <ol>
 * <li>Separation of interface and implementation</li>
 * <li>Generics argument to the update method</li>
 * <li>and foremost: The update method can be called on the Event Dispatch Thread (EDT) if this is
 * desired. If you don't know, what the EDT is, read the Java docs on Swing and synchronization and
 * use EDT if you register an observer that updates Swing GUI components.</li>
 * <li>synchronization (Sun's implementation may be synchronized, too, but it is not documented)</li>
 * <li>defined exception handling.</li>
 * </ol>
 *
// * */
public interface Observable<A> {

	/**
	 * Adds an observer that will be notified on the EDT if onEDT is true.
	 *
	 * @throws a
	 *             NPE if observer is null.
	 */
	public void addObserver(Observer<A> observer, boolean onEDT);

	/**
	 * Removes an observer from this observable.
	 *
	 * @throws NoSuchElementException
	 *             if observer is not registered with this Observable.
	 */
	public void removeObserver(Observer<A> observer);

	/**
	 * Same as {@link #addObserver(Observer, boolean), but adds this observer as the first in the
	 * list.
	 */
	void addObserverAsFirst(Observer<A> observer, boolean onEDT);

}
