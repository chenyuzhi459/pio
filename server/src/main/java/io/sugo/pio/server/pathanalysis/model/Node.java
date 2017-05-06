package io.sugo.pio.server.pathanalysis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.server.pathanalysis.PathAnalysisConstant;

/**
 */
public class Node implements Comparable<Node> {

    /**
     * The page name
     */
    private String pageName;

    /**
     * The layer where this node in the access path
     */
    private int layer;

    public Node(String pageName, int layer) {
        this.pageName = pageName;
        this.layer = layer;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Node)) {
            return false;
        }

        Node o = (Node) other;
        return (this.pageName.equals(o.pageName)) && (this.layer == o.layer);
    }

    @Override
    public int hashCode() {
        return this.pageName.hashCode() ^ Double.valueOf(this.layer).hashCode();
    }

    @Override
    public int compareTo(Node other) {
        if (layer < other.getLayer()) {
            return -1;
        } else if (layer > other.getLayer()) {
            return 1;
        } else {
            return pageName.compareTo(other.getPageName());
        }
    }

    @JsonProperty
    public String getPageName() {
        return pageName;
    }

    @JsonProperty
    public int getLayer() {
        return layer;
    }
}
