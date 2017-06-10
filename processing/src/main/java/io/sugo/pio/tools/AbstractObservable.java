package io.sugo.pio.tools;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;


/**
 */
public class AbstractObservable<A> implements Observable<A> {

	private final LinkedList<Observer<A>> observersRegular = new LinkedList<>();
	private final LinkedList<Observer<A>> observersEDT = new LinkedList<>();

	private final Object lock = new Object();

	@Override
	public void addObserverAsFirst(Observer<A> observer, boolean onEDT) {
		if (observer == null) {
			throw new NullPointerException("Observer is null.");
		}
		if (onEDT) {
			synchronized (lock) {
				observersEDT.addFirst(observer);
			}
		} else {
			synchronized (lock) {
				observersRegular.addFirst(observer);
			}
		}
	}

	@Override
	public void addObserver(Observer<A> observer, boolean onEDT) {
		if (observer == null) {
			throw new NullPointerException("Observer is null.");
		}
		if (onEDT) {
			synchronized (lock) {
				observersEDT.add(observer);
			}
		} else {
			synchronized (lock) {
				observersRegular.add(observer);
			}
		}
	}

	@Override
	public void removeObserver(Observer<A> observer) {
		boolean success = false;
		synchronized (lock) {
			success |= observersRegular.remove(observer);
			success |= observersEDT.remove(observer);
		}
		if (!success) {
			throw new NoSuchElementException("No such observer: " + observer);
		}
	}

	/** Updates the observers in the given list. */
	private void fireUpdate(List<Observer<A>> observerList, A argument) {
		for (Observer<A> observer : observerList) {
			observer.update(this, argument);
		}
	}

	/** Equivalent to <code>fireUpdate(null)</code>. */
	protected void fireUpdate() {
		fireUpdate(null);
	}

	/** Updates all observers with the given argument. */
	protected void fireUpdate(final A argument) {
		// lists are copied in order to avoid ConcurrentModification occurs if updating
		// an observer triggers insertion of another
		List<Observer<A>> copy;
		synchronized (lock) {
			copy = new LinkedList<>(observersRegular);
		}
		fireUpdate(copy, argument);
		if (!observersEDT.isEmpty()) {
			final List<Observer<A>> copyEDT;
			synchronized (lock) {
				copyEDT = new LinkedList<>(observersEDT);
			}
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					fireUpdate(copyEDT, argument);
				}
			});
		}
	}

}
