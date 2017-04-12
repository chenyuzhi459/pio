package io.sugo.pio.server.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 */
public class CustomizedRFMDto extends RFMDto {

    private RFM params;

    private static class RFM {
        @JsonProperty("R")
        double[] rq;
        @JsonProperty("F")
        double[] fq;
        @JsonProperty("M")
        double[] mq;

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
    }

    public RFM getParams() {
        return params;
    }

    public void setParams(RFM params) {
        this.params = params;
    }

    public double[] getRq() {
        return this.params.rq;
    }

    public double[] getFq() {
        return this.params.fq;
    }

    public double[] getMq() {
        return this.params.mq;
    }

}
