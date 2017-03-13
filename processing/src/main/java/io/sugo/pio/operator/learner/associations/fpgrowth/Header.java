package io.sugo.pio.operator.learner.associations.fpgrowth;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * An entry in the header table.
 * 
 */
public class Header {

	FrequencyStack frequencies;

	List<FPTreeNode> siblingChain;

	public Header() {
		frequencies = new ListFrequencyStack();
		siblingChain = new LinkedList<FPTreeNode>();
	}

	public void addSibling(FPTreeNode node) {
		siblingChain.add(node);
	}

	public Collection<FPTreeNode> getSiblingChain() {
		return siblingChain;
	}

	public FrequencyStack getFrequencies() {
		return frequencies;
	}
}
