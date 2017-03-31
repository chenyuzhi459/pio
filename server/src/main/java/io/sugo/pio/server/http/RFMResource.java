package io.sugo.pio.server.http;


import com.metamx.common.logger.Logger;

import javax.ws.rs.Path;

@Path("/pio/process/rfm/")
public class RFMResource {
    private static final Logger log = new Logger(RFMResource.class);

    java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");

    public static final int[] arr = new int[]{1, 3, 4, 8, 9, 15, 18, 25, 40, 42, 56, 60, 66, 68, 72, 77, 79, 85, 88, 96};

    public double[] calculateQuantile(int quantileNum) {
        double[] quantiles = new double[quantileNum-1];

        // Calculate the average step
        double step = (arr.length + 1.0) / quantileNum;

        for (int i = 0; i < quantileNum-1; i++) {
            quantiles[i] = getQuantilePoint(i+1, step);
        }

        return quantiles;
    }

    public double getQuantilePoint(int quantileIndex, double step) {
        double nStep = quantileIndex * step;
        int integerPart = (int) nStep;
        double decimalPart = nStep - integerPart;

        double quantilePoint = arr[integerPart-1] + (arr[integerPart] - arr[integerPart-1]) * decimalPart;
        return Double.valueOf(df.format(quantilePoint));
    }

    public static void main(String[] args) {
        for (int i = 0; i < RFMResource.arr.length; i++) {
            System.out.print(RFMResource.arr[i] + ",");
        }
        System.out.println();

        RFMResource rfm = new RFMResource();
        double[] quantiles = rfm.calculateQuantile(4);
        for (int i = 0; i < quantiles.length; i++) {
            System.out.println((i+1) + "分位值为：" + quantiles[i]);
        }
    }

}
