package io.sugo.pio.operator.learner.associations.fpgrowth;

import java.util.ArrayList;
import java.util.List;


/**
 * A frequency stack based on a list implementation.
 *
 */
public class ListFrequencyStack implements FrequencyStack {

	private List<Integer> list;

	public ListFrequencyStack() {
		list = new ArrayList<Integer>();
	}

	@Override
	public int getFrequency(int height) {
		if (height >= list.size()) {
			return 0;
		} else {
			return list.get(height);
		}
	}

	@Override
	public void increaseFrequency(int stackHeight, int value) {
		if (stackHeight == list.size() - 1) {
			// int newValue = value + list.pollLast(); // IM: pollLast only
			// available in JDK 6
			int newValue = value + list.remove(stackHeight);
			list.add(newValue);
		} else if (stackHeight == list.size()) {
			list.add(value);
		}
	}

	@Override
	public void popFrequency(int height) {
		if (height <= list.size() - 1) {
			list.remove(height);
		}
	}
}
