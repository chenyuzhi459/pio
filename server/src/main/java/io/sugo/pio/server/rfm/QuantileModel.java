package io.sugo.pio.server.rfm;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.TreeMap;

/**
 */
public class QuantileModel {

    /**
     * The quantile number of recency
     */
    @JsonProperty
    private int r;

    /**
     * The quantile number of frequency
     */
    @JsonProperty
    private int f;

    /**
     * The quantile number of monetary
     */
    @JsonProperty
    private int m;

    /**
     * The quantile of recency
     */
    @JsonProperty
    private double[] rq;

    /**
     * The quantile of frequency
     */
    @JsonProperty
    private double[] fq;

    /**
     * The quantile of monetary
     */
    @JsonProperty
    private double[] mq;

    @JsonProperty
    private String[] rLabels;
    @JsonProperty
    private String[] fLabels;
    @JsonProperty
    private String[] mLabels;

    @JsonProperty
    private Map<String, Integer> groupMap;

    public QuantileModel(int r, int f, int m) {
        this.r = r;
        this.f = f;
        this.m = m;
        rq = new double[r];
        fq = new double[f];
        mq = new double[m];

        initLabels(r, f, m);
        initGroup();
    }

    private void initLabels(int r, int f, int m) {
        rLabels = new String[r];
        fLabels = new String[f];
        mLabels = new String[m];

        for (int i = 0; i < r; i++) {
            rLabels[i] = "R" + (i + 1);
        }
        for (int i = 0; i < f; i++) {
            fLabels[i] = "F" + (i + 1);
        }
        for (int i = 0; i < m; i++) {
            mLabels[i] = "M" + (i + 1);
        }
    }

    private void initGroup() {
        groupMap = new TreeMap<>();
        for (String rLabel : rLabels) {
            for (String fLabel : fLabels) {
                for (String mLabel : mLabels) {
                    groupMap.put(rLabel + ":" + fLabel + ":" + mLabel, 0);
                }
            }
        }
    }

    public String getRLabel(int recency) {
        for (int i = 0; i < rq.length; i++) {
            if (recency <= rq[i]) {
                return rLabels[i];
            }
        }
        return rLabels[rLabels.length - 1];
    }

    public String getFLabel(int frequency) {
        for (int i = 0; i < fq.length; i++) {
            if (frequency <= fq[i]) {
                return fLabels[i];
            }
        }
        return fLabels[fLabels.length - 1];
    }

    public String getMLabel(double monetary) {
        for (int i = 0; i < mq.length; i++) {
            if (monetary <= mq[i]) {
                return mLabels[i];
            }
        }
        return mLabels[mLabels.length - 1];
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public double[] getRq() {
        return rq;
    }

    public void setRq(double[] rq) {
        this.rq = rq;
    }

    public double[] getFq() {
        return fq;
    }

    public void setFq(double[] fq) {
        this.fq = fq;
    }

    public double[] getMq() {
        return mq;
    }

    public void setMq(double[] mq) {
        this.mq = mq;
    }

    public Map<String, Integer> getGroupMap() {
        return groupMap;
    }
}
