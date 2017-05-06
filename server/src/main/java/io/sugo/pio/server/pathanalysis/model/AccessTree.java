package io.sugo.pio.server.pathanalysis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.collect.Lists;
import io.sugo.pio.server.pathanalysis.PathAnalysisConstant;

import java.io.Serializable;
import java.util.List;

/**
 */
public class AccessTree implements Serializable {

    private static final long serialVersionUID = 1004553939220527137L;

    private static final java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");

    private PathNode leaf;

    private int weight;

    private String rate = "";

    private List<AccessTree> children = Lists.newLinkedList();

    public AccessTree(PathNode leaf, int weight) {
        this.leaf = leaf;
        this.weight = weight;
    }

    public void addChild(AccessTree child) {
        children.add(child);
    }

    public void increaseWeight() {
        weight++;
    }

    private void prune() {
        // First, descent sort by weight.
        children.sort((tom, jack) ->
                tom.getWeight() > jack.getWeight() ? -1 :
                        tom.getWeight() < jack.getWeight() ? 1 : 0);

        // Second, merge trees into a tree which beyond the number of 'MAX_CHILDREN'.
        if (children.size() > PathAnalysisConstant.MAX_CHILDREN) {
            List<AccessTree> tailChildren = children.subList(PathAnalysisConstant.MAX_CHILDREN,
                    children.size());
            AccessTree mergedTree = merge(tailChildren);
            children.removeAll(tailChildren);
            children.add(mergedTree);
        }

        // Third, add tree which indicate user loss if necessary.
        if (children.size() > 0) {
            int totalWeight = 0;
            for (AccessTree child : children) {
                totalWeight += child.getWeight();
            }
            if (weight > totalWeight) {
                int lostWeight = weight - totalWeight;
                AccessTree lossTree = new AccessTree(new PathNode(PathAnalysisConstant.LEAF_NAME_LOSS,
                        children.get(0).getLeaf().getLayer(), PathAnalysisConstant.NODE_TYPE_LOSS),
                        lostWeight);
                children.add(lossTree);
            }

            // Last, calculate the rate of each child.
            for (AccessTree child : children) {
                String rate = Double.valueOf(df.format(child.getWeight() * 100.0d / weight)) + "%";
                child.setRate(rate);
            }
        }
    }

    private AccessTree merge(List<AccessTree> trees) {
        int totalWeight = 0;
        for (AccessTree tree : trees) {
            totalWeight += tree.getWeight();
        }

        return new AccessTree(new PathNode(PathAnalysisConstant.LEAF_NAME_OTHER,
                trees.get(0).getLeaf().getLayer()), totalWeight);
    }

    @JsonProperty
    public List<AccessTree> getChildren() {
        prune();
        return children;
    }

    @JsonProperty
    public String getRate() {
        return rate;
    }

    @JsonProperty
    public int getWeight() {
        return weight;
    }

    @JsonUnwrapped
    public PathNode getLeaf() {
        return leaf;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
