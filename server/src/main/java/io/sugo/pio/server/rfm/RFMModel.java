package io.sugo.pio.server.rfm;

/**
 */
public class RFMModel {

    public static final int R = 0;
    public static final int F = 1;
    public static final int M = 2;

    private String userId;

    private int recency;
    private int frequency;
    private double monetary;

    private String rLabel;
    private String fLabel;
    private String mLabel;

    public String getGroup() {
        return rLabel + ":" + fLabel + ":" + mLabel;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRecency() {
        return recency;
    }

    public void setRecency(int recency) {
        this.recency = recency;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public double getMonetary() {
        return monetary;
    }

    public void setMonetary(double monetary) {
        this.monetary = monetary;
    }

    public String getrLabel() {
        return rLabel;
    }

    public RFMModel setrLabel(String rLabel) {
        this.rLabel = rLabel;
        return this;
    }

    public String getfLabel() {
        return fLabel;
    }

    public RFMModel setfLabel(String fLabel) {
        this.fLabel = fLabel;
        return this;
    }

    public String getmLabel() {
        return mLabel;
    }

    public RFMModel setmLabel(String mLabel) {
        this.mLabel = mLabel;
        return this;
    }
}
