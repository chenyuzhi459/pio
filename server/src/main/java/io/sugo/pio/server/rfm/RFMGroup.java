package io.sugo.pio.server.rfm;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

/**
 */
public class RFMGroup {

    /**
     * The name of this group
     */
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
    private List<String> userIdList = Collections.emptyList();

    @JsonProperty("R")
    private double[] rRange;
    @JsonProperty("F")
    private double[] fRange;
    @JsonProperty("M")
    private double[] mRange;

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

    public double[] getrRange() {
        return rRange;
    }

    public void setrRange(double[] rRange) {
        this.rRange = rRange;
    }

    public double[] getfRange() {
        return fRange;
    }

    public void setfRange(double[] fRange) {
        this.fRange = fRange;
    }

    public double[] getmRange() {
        return mRange;
    }

    public void setmRange(double[] mRange) {
        this.mRange = mRange;
    }

    public int getRGroupIndex() {
        return Integer.valueOf(this.name.split(":")[0].substring(1));
    }

    public int getFGroupIndex() {
        return Integer.valueOf(this.name.split(":")[1].substring(1));
    }

    public int getMGroupIndex() {
        return Integer.valueOf(this.name.split(":")[2].substring(1));
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
