package io.sugo.pio.operator.learner.tree;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;


/**
 * The class edge holds the information about a split condition to a tree (child).
 * 
 * @author Sebastian Land, Ingo Mierswa
 */
public class Edge implements Serializable, Comparable<Edge> {

	private static final long serialVersionUID = -6470281011799533198L;

	@JsonProperty
	private SplitCondition condition;

	@JsonProperty
	private Tree child;

	public Edge(Tree child, SplitCondition condition) {
		this.condition = condition;
		this.child = child;
	}

	public SplitCondition getCondition() {
		return this.condition;
	}

	public Tree getChild() {
		return this.child;
	}

	@Override
	public int compareTo(Edge o) {
		return (this.condition.getRelation() + this.condition.getValueString()).compareTo(o.condition.getRelation()
				+ o.condition.getValueString());
	}
}
