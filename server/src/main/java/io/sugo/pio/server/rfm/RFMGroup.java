package io.sugo.pio.server.rfm;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 */
public class RFMGroup {

    /**
     * The name of this group
     */
    @JsonProperty
    private String name;

    /**
     * The number of users of this group
     */
    @JsonProperty
    private int userCount;

    /**
     * The percent of users of this group
     */
    @JsonProperty
    private String userPercent;

    /**
     * The user id list that belong to this group
     */
    @JsonProperty
    private List<String> userIdList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public String getUserPercent() {
        return userPercent;
    }

    public void setUserPercent(String userPercent) {
        this.userPercent = userPercent;
    }

    public List<String> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<String> userIdList) {
        this.userIdList = userIdList;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("[RFMGroup]");
        sb.append("name:").append(name).append(", ")
                .append("userCount:").append(userCount).append(", ")
                .append("userPercent:").append(userPercent);

        return sb.toString();
    }

}
