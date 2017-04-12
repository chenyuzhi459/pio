package io.sugo.pio.server.rfm;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 */
public class QuantileModel {

    private static final java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");

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
    private double[] rq;

    /**
     * The quantile of frequency
     */
    private double[] fq;

    /**
     * The quantile of monetary
     */
    private double[] mq;

    private double[][] rRanges;
    private double[][] fRanges;
    private double[][] mRanges;

    private String[] rLabels;
    private String[] fLabels;
    private String[] mLabels;

    @JsonProperty
    private List<RFMGroup> groups;

    private Map<String, Integer> groupMap;

    public QuantileModel(int r, int f, int m) {
        this.r = r;
        this.f = f;
        this.m = m;

        initLabels(r, f, m);
        initGroup();
    }

    public static QuantileModel emptyModel(int r, int f, int m) {
        return new QuantileModel(r, f, m);
    }

    public static QuantileModel emptyModel(double[] rq, double[] fq, double[] mq) {
        QuantileModel model = new QuantileModel(0, 0, 0);
        model.setRq(rq);
        model.setFq(fq);
        model.setMq(mq);

        return model;
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

    public void buildGroups(Map<String, List<String>> groupUserIdsMap, int totalUsers) {
        groups = new ArrayList<>();
        groupMap.entrySet().iterator().forEachRemaining((entry) -> {
            RFMGroup group = new RFMGroup();
            group.setName(entry.getKey());
            group.setUserCount(entry.getValue());
            group.setUserPercent(Double.valueOf(df.format(entry.getValue() * 100.0d / totalUsers)) + "%");
            if (groupUserIdsMap.get(entry.getKey()) != null) {
                group.setUserIdList(groupUserIdsMap.get(entry.getKey()));
            }
            group.setrRange(getrRanges(group.getRGroupIndex()));
            group.setfRange(getfRanges(group.getFGroupIndex()));
            group.setmRange(getmRanges(group.getMGroupIndex()));

            groups.add(group);
        });
    }

    public double[] getrRanges(int groupIndex) {
        if (rRanges == null) {
            initRanges(RFMModel.R);
        }

        return rRanges[groupIndex-1];
    }

    public double[] getfRanges(int groupIndex) {
        if (fRanges == null) {
            initRanges(RFMModel.F);
        }

        return fRanges[groupIndex-1];
    }

    public double[] getmRanges(int groupIndex) {
        if (mRanges == null) {
            initRanges(RFMModel.M);
        }

        return mRanges[groupIndex-1];
    }

    public void initRanges(int dimension) {
        double[] q = null;
        double[][] ranges = null;
        switch (dimension) {
            case RFMModel.R:
                q = rq;
                ranges = new double[rq.length + 1][];
                rRanges = ranges;
                break;
            case RFMModel.F:
                q = fq;
                ranges = new double[fq.length + 1][];
                fRanges = ranges;
                break;
            case RFMModel.M:
                q = mq;
                ranges = new double[mq.length + 1][];
                mRanges = ranges;
                break;
        }

        for (int i = 0; i <= q.length; i++) {
            if (i == 0) {
                double[] range = new double[2];
                range[0] = 0;
                range[1] = q[0];
                ranges[0] = range;
            } else if (i == q.length) {
                double[] range = new double[1];
                range[0] = q[q.length - 1];
                ranges[q.length] = range;
            } else {
                double[] range = new double[2];
                range[0] = q[i - 1];
                range[1] = q[i];
                ranges[i] = range;
            }
        }
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

    @Override
    public String toString() {
        String spliter = "";
        StringBuffer sb = new StringBuffer("[QuantileModel]");
        sb.append("r:").append(r).append(", ")
                .append("f:").append(f).append(", ")
                .append("m:").append(m).append(", ")
                .append("rq: {");
        for (double e : rq) {
            sb.append(spliter).append(e);
            spliter = ",";
        }
        spliter = "";
        sb.append("}, fq: {");
        for (double e : fq) {
            sb.append(spliter).append(e);
            spliter = ",";
        }
        spliter = "";
        sb.append("}, mq: {");
        for (double e : mq) {
            sb.append(spliter).append(e);
            spliter = ",";
        }
        spliter = "";
        sb.append("}, rLabels: {");
        for (String e : rLabels) {
            sb.append(spliter).append(e);
            spliter = ",";
        }
        spliter = "";
        sb.append("}, fLabels: {");
        for (String e : fLabels) {
            sb.append(spliter).append(e);
            spliter = ",";
        }
        spliter = "";
        sb.append("}, mLabels: {");
        for (String e : mLabels) {
            sb.append(spliter).append(e);
            spliter = ",";
        }
        sb.append("}, groups:").append(groups);

        return sb.toString();
    }
}
