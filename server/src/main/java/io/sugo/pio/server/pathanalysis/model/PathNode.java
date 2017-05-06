package io.sugo.pio.server.pathanalysis.model;

/**
 */
public class PathNode extends Node {

    /**
     * The stay time of the user on this node
     */
    private long stayTime;

    private String userId;

    public PathNode(String pageName, int layer) {
        super(pageName, layer);
    }

    public long getStayTime() {
        return stayTime;
    }

    public void setStayTime(long stayTime) {
        this.stayTime = stayTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
