package io.sugo.pio.server.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 */
public class DefaultRFMDto extends RFMDto {

    private RFM params;

    private static class RFM {
        @JsonProperty("R")
        int r;
        @JsonProperty("F")
        int f;
        @JsonProperty("M")
        int m;

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
    }

    public RFM getParams() {
        return params;
    }

    public void setParams(RFM params) {
        this.params = params;
    }

    public int getR() {
        return this.params.r;
    }

    public int getF() {
        return this.params.f;
    }

    public int getM() {
        return this.params.m;
    }

}
