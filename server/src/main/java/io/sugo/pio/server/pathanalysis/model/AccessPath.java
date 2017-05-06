package io.sugo.pio.server.pathanalysis.model;

import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

/**
 */
public class AccessPath implements Comparable<AccessPath> {

    /**
     * All the nodes of this path. Note that the nodes is in order
     */
    private List<PathNode> nodeList = Lists.newArrayList();

    public void addNode(PathNode node) {
        if (!nodeList.contains(node)) {
            node.setAccessPath(this);
            nodeList.add(node);
        }
    }

    public PathNode getNodeOfLayer(int layer) {
        try {
            return nodeList.get(layer);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public int length() {
        return nodeList.size();
    }

    /**
     * Returns true if the node list are equal in size and items.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof AccessPath) {
            return (this.compareTo((AccessPath) o) == 0);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return nodeList.hashCode();
    }

    @Override
    public int compareTo(AccessPath other) {
        // compare size first
        List<PathNode> hisNodeList = other.getNodeList();
        if (nodeList.size() < hisNodeList.size()) {
            return -1;
        } else if (nodeList.size() > hisNodeList.size()) {
            return 1;
        } else {
            // compare nodes
            Iterator<PathNode> iterator = hisNodeList.iterator();
            for (PathNode node : nodeList) {
                int relation = node.compareTo(iterator.next());
                if (relation != 0) {
                    return relation;
                }
            }
            // All corresponding nodes are equal
            return 0;
        }
    }

    public List<PathNode> getNodeList() {
        return nodeList;
    }

}
