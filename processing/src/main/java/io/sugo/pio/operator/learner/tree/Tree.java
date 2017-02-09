/**
 * Copyright (C) 2001-2016 by RapidMiner and the contributors
 *
 * Complete list of developers available at our web site:
 *
 * http://rapidminer.com
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */
package io.sugo.pio.operator.learner.tree;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.tools.Tools;

import java.io.Serializable;
import java.util.*;


/**
 * A tree is a node in a tree model containing several edges to other trees (children) combined with
 * conditions at these edges.
 *
 * Leafs contain the class label which should be predicted.
 *
 * @author Sebastian Land, Ingo Mierswa
 */
public class Tree implements Serializable {

	private static final long serialVersionUID = -5930873649086170840L;

	@JsonProperty
	private String label = null;

	@JsonProperty
	private List<Edge> children = new LinkedList<Edge>();

	private Map<String, Integer> counterMap = new LinkedHashMap<String, Integer>();

	private transient ExampleSet trainingSet = null;

	public Tree(ExampleSet trainingSet) {
		this.trainingSet = trainingSet;
	}

	public ExampleSet getTrainingSet() {
		return this.trainingSet;
	}

	public void addCount(String className, int count) {
		counterMap.put(className, count);
	}

	public int getCount(String className) {
		Integer count = counterMap.get(className);
		if (count == null) {
			return 0;
		} else {
			return count;
		}
	}

	public int getFrequencySum() {
		int sum = 0;
		for (Integer i : counterMap.values()) {
			sum += i;
		}
		return sum;
	}

	public int getSubtreeFrequencySum() {
		if (children.size() == 0) {
			return getFrequencySum();
		} else {
			int sum = 0;
			for (Edge edge : children) {
				sum += edge.getChild().getSubtreeFrequencySum();
			}
			return sum;
		}
	}

	/**
	 * This returns the class counts from all contained examples by iterating recursively through
	 * the tree.
	 */
	public Map<String, Integer> getSubtreeCounterMap() {
		Map<String, Integer> counterMap = new LinkedHashMap<String, Integer>();
		fillSubtreeCounterMap(counterMap);
		return counterMap;
	}

	protected void fillSubtreeCounterMap(Map<String, Integer> counterMap) {
		if (children.size() == 0) {
			// then its leaf: Add all counted frequencies
			for (String key : this.counterMap.keySet()) {
				int newValue = this.counterMap.get(key);
				if (counterMap.containsKey(key)) {
					newValue += counterMap.get(key);
				}
				counterMap.put(key, newValue);
			}
		} else {
			for (Edge edge : children) {
				edge.getChild().fillSubtreeCounterMap(counterMap);
			}
		}
	}

	public Map<String, Integer> getCounterMap() {
		return counterMap;
	}

	public void setLeaf(String label) {
		this.label = label;
	}

	public void addChild(Tree child, SplitCondition condition) {
		this.children.add(new Edge(child, condition));
		Collections.sort(this.children);
	}

	public void removeChildren() {
		this.children.clear();
	}

	public boolean isLeaf() {
		return children.size() == 0;
	}

	public String getLabel() {
		return this.label;
	}

	public Iterator<Edge> childIterator() {
		return children.iterator();
	}

	public int getNumberOfChildren() {
		return children.size();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		toString(null, this, "", buffer, Tools.getLineSeparator());
		return buffer.toString();
	}

	public String toString(String separator) {
		StringBuffer buffer = new StringBuffer();
		toString(null, this, "", buffer, separator);
		return buffer.toString();
	}

	private void toString(SplitCondition condition, Tree tree, String indent, StringBuffer buffer, String separator) {
		if (condition != null) {
			buffer.append(condition.toString());
		}
		if (!tree.isLeaf()) {
			Iterator<Edge> childIterator = tree.childIterator();
			while (childIterator.hasNext()) {
				buffer.append(separator);
//				buffer.append(Tools.getWebLineSeparator());
				buffer.append(indent);
				Edge edge = childIterator.next();
				toString(edge.getCondition(), edge.getChild(), indent + "|   ", buffer, separator);
			}
		} else {
			buffer.append(": ");
			buffer.append(tree.getLabel());
			buffer.append(" " + tree.counterMap.toString());
		}
	}
}
