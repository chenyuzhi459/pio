package io.sugo.pio.server.pathanalysis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.server.pathanalysis.PathAnalysisConstant;

/**
 */
public class PathNode implements Comparable<PathNode> {

    /**
     * The page name
     */
    private String pageName;

    /**
     * The layer where this node in the access path
     */
    private int layer;

    /**
     * Statistic the total stay time of the user on this node
     */
    private int stayTime;

    /**
     * The node type
     */
    private int type = PathAnalysisConstant.NODE_TYPE_RETENTION;

    /**
     * The access path which this node belongs to
     */
    private AccessPath accessPath;

    public void accumulateStayTime(int time) {
        stayTime += time;
    }

    public PathNode(String pageName, int layer) {
        this.pageName = pageName;
        this.layer = layer;
    }

    public PathNode(String pageName, int layer, int type) {
        this(pageName, layer);
        this.type = type;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof PathNode)) {
            return false;
        }

        PathNode o = (PathNode) other;
        return (this.pageName.equals(o.pageName)) && (this.layer == o.layer);
    }

    @Override
    public int hashCode() {
        return this.pageName.hashCode() ^ Double.valueOf(this.layer).hashCode();
    }

    @Override
    public int compareTo(PathNode other) {
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

    @JsonProperty
    public int getType() {
        return type;
    }

    public AccessPath getAccessPath() {
        return accessPath;
    }

    public void setAccessPath(AccessPath accessPath) {
        this.accessPath = accessPath;
    }

    public int getStayTime() {
        return stayTime;
    }
}
