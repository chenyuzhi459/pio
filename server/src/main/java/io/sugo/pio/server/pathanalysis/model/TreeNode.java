package io.sugo.pio.server.pathanalysis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import io.sugo.pio.server.pathanalysis.PathAnalysisConstant;

import java.util.Set;

/**
 */
public class TreeNode extends Node {

    /**
     * Statistic the total stay time of the user on this node
     */
    private long totalStayTime;

    private Set<String> userIds = Sets.newHashSet();

    /**
     * The node type
     */
    private int type = PathAnalysisConstant.NODE_TYPE_RETENTION;

    public TreeNode(String pageName, int layer) {
        super(pageName, layer);
    }

    public TreeNode(String pageName, int layer, int type) {
        super(pageName, layer);
        this.type = type;
    }

    public static TreeNode convert(PathNode pathNode) {
        if (pathNode == null) {
            return null;
        }

        TreeNode treeNode = new TreeNode(pathNode.getPageName(), pathNode.getLayer());
        treeNode.setTotalStayTime(pathNode.getStayTime());
        if (pathNode.getUserId() != null) {
            treeNode.addUserId(pathNode.getUserId());
        }

        return treeNode;
    }

    @JsonProperty
    public int getType() {
        return type;
    }

    public void accumulateStayTime(long time) {
        totalStayTime += time;
    }

    public long getTotalStayTime() {
        return totalStayTime;
    }

    public void setTotalStayTime(long totalStayTime) {
        this.totalStayTime = totalStayTime;
    }

    @JsonProperty
    public Set<String> getUserIds() {
        return userIds;
    }

    public TreeNode setUserIds(Set<String> userIds) {
        this.userIds = userIds;
        return this;
    }

    public void addUserId(String userId) {
        userIds.add(userId);
    }

    public void setType(int type) {
        this.type = type;
    }
}
