package io.sugo.pio.server.rfm;

import java.util.Calendar;
import java.util.Date;

/**
 */
public class RFMModel {

    private static final Calendar calendar = Calendar.getInstance();

    public static final int R = 0;
    public static final int F = 1;
    public static final int M = 2;

    private String userId;

    private int recency;
    private int frequency;
    private double monetary;

    /**
     * Last purchase time
     */
    private Date lastTime;

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
        if (recency == 0) {
            calendar.setTime(lastTime);
            int dayBefore = calendar.get(Calendar.DAY_OF_YEAR);

            calendar.setTime(new Date());
            int dayNow = calendar.get(Calendar.DAY_OF_YEAR);

            recency = dayNow - dayBefore;
        }
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

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
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
