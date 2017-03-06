package io.sugo.pio.operator.learner.functions.kernel.jmysvm.util;

/**
 * Implements a MinHeap on n doubles and ints
 * 
 */
public class MinHeap extends Heap {

	public MinHeap(int n) {
		the_size = 0;
		init(n);
	};

	@Override
	public final void add(double value, int index) {
		if (last < the_size) {
			heap[last] = value;
			indizes[last] = index;
			last++;
			if (last == the_size) {
				for (int j = last; j > 0; j--) {
					heapify(j - 1, last + 1 - j);
				}
				;
			}
			;
		} else if (value < heap[0]) {
			heap[0] = value;
			indizes[0] = index;
			heapify(0, last);
		}
		;
	};

	@Override
	protected final void heapify(int start, int size) {
		double[] my_heap = heap;
		boolean running = true;
		int pos = 1;
		int left, right, largest;
		double dummyf;
		int dummyi;
		start--; // other variables counted from 1
		while (running) {
			left = 2 * pos;
			right = left + 1;
			if ((left <= size) && (my_heap[left + start] > my_heap[start + pos])) {
				largest = left;
			} else {
				largest = pos;
			}
			;
			if ((right <= size) && (my_heap[start + right] > my_heap[start + largest])) {
				largest = right;
			}
			;
			if (largest == pos) {
				running = false;
			} else {
				dummyf = my_heap[start + pos];
				dummyi = indizes[start + pos];
				my_heap[start + pos] = my_heap[start + largest];
				indizes[start + pos] = indizes[start + largest];
				my_heap[start + largest] = dummyf;
				indizes[start + largest] = dummyi;
				pos = largest;
			}
			;
		}
		;
	};
};
