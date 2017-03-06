package io.sugo.pio.operator.learner.functions.kernel.jmysvm.util;

/**
 * Implements a Heap on n doubles and ints
 * 
 */
public abstract class Heap {

	protected int the_size;

	protected int last;

	protected double[] heap;

	protected int[] indizes;

	public Heap() {};

	public Heap(int n) {
		the_size = 0;
		init(n);
	};

	public int size() {
		return last; // last = number of elements
	};

	public void init(int n) {
		if (the_size != n) {
			the_size = n;
			heap = new double[n];
			indizes = new int[n];
		}
		;
		last = 0;
	};

	public void clear() {
		the_size = 0;
		last = 0;
		heap = null;
		indizes = null;
	};

	public int[] get_values() {
		return indizes;
	};

	public abstract void add(double value, int index);

	public double top_value() {
		return heap[0];
	};

	public boolean empty() {
		return (last == 0);
	};

	protected abstract void heapify(int start, int size);

};
