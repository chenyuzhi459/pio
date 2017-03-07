package io.sugo.pio.operator.learner.associations.fpgrowth;

import io.sugo.pio.operator.learner.associations.Item;
import io.sugo.pio.tools.Tools;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * This is the basic data structure for {@link FPGrowth}.
 * 
 */
public class FPTree extends FPTreeNode {

	private Map<Item, Header> headerTable;

	public FPTree() {
		super();
		headerTable = new HashMap<Item, Header>();
		children = new HashMap<Item, FPTreeNode>();
	}

	/**
	 * This method adds a set of Items to the tree. This set of items has to be sorted after the
	 * frequency of the contained items. This method should be used to add Items of a transaction or
	 * a treepath to the tree. The frequency of the set is represented of weight, which should be 1
	 * if items are gathered from transaction
	 * 
	 * @param itemSet
	 *            the sorted set of items
	 * @param weight
	 *            the frequency of the set of items
	 */
	public void addItemSet(Collection<Item> itemSet, int weight) {
		super.addItemSet(itemSet, headerTable, weight);
	}

	public Map<Item, Header> getHeaderTable() {
		return headerTable;
	}

	@Override
	public String toString(String abs, int recursionDepth) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(abs);
		buffer.append("+ ROOT");
		buffer.append(Tools.getLineSeparator());
		for (FPTreeNode node : children.values()) {
			buffer.append(node.toString(abs + "  ", recursionDepth));
		}
		return buffer.toString();
	}

	public String printHeaderTable(int recursionDepth) {
		StringBuffer buffer = new StringBuffer();
		for (Item item : headerTable.keySet()) {
			buffer.append(item.toString());
			buffer.append(" : ");
			buffer.append(headerTable.get(item).getFrequencies().getFrequency(recursionDepth));
			buffer.append(Tools.getLineSeparator());
		}
		return buffer.toString();
	}
}
