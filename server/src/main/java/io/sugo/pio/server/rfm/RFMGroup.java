package io.sugo.pio.server.rfm;

import com.fasterxml.jackson.annotation.JsonProperty;

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
}
