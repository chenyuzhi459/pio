package io.sugo.pio.server.rfm;

import java.util.List;
import java.util.Map;

/**
 */
public class DefaultQuantileCalculator extends AbstractQuantileCalculator {

    private static final java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");

    /**
     * The quantile number of recency
     */
    private int r;

    /**
     * The quantile number of frequency
     */
    private int f;

    /**
     * The quantile number of monetary
     */
    private int m;

    public DefaultQuantileCalculator(List<RFMModel> rfmModelList, int r, int f, int m) {
        super(rfmModelList);
        this.r = r;
        this.f = f;
        this.m = m;
    }

    @Override
    public void initModel() {
        quantileModel = new QuantileModel(r, f, m);
        quantileModel.setRq(calculateR());
        quantileModel.setFq(calculateF());
        quantileModel.setMq(calculateM());
    }

    private double[] calculateR() {
        // Sort the model list by recency
        rfmModelList.sort((model1, model2) ->
                model1.getRecency() > model2.getRecency() ? 1 :
                        model1.getRecency() < model2.getRecency() ? -1 : 0);

        return calculateQuantile(r, RFMModel.R);
    }

    private double[] calculateF() {
        // Sort the model list by frequency
        rfmModelList.sort((model1, model2) ->
                model1.getFrequency() > model2.getFrequency() ? 1 :
                        model1.getFrequency() < model2.getFrequency() ? -1 : 0);

        return calculateQuantile(f, RFMModel.F);
    }

    private double[] calculateM() {
        // Sort the model list by monetary
        rfmModelList.sort((model1, model2) ->
                model1.getMonetary() > model2.getMonetary() ? 1 :
                        model1.getMonetary() < model2.getMonetary() ? -1 : 0);

        return calculateQuantile(m, RFMModel.M);
    }

    private double[] calculateQuantile(int quantileNum, int dimension) {
        double[] quantiles = new double[quantileNum - 1];

        // Calculate the average step
        double step = (rfmModelList.size() + 1.0) / quantileNum;

        for (int i = 0; i < quantileNum - 1; i++) {
            quantiles[i] = getQuantilePoint(i + 1, step, dimension);
        }

        return quantiles;
    }

    private double getQuantilePoint(int quantileIndex, double step, int dimension) {
        double nStep = quantileIndex * step;
        int integerPart = (int) nStep;
        double decimalPart = nStep - integerPart;

        double quantilePoint = 0.0;
        switch (dimension) {
            case RFMModel.R:
                quantilePoint = rfmModelList.get(integerPart - 1).getRecency() +
                        (rfmModelList.get(integerPart).getRecency() - rfmModelList.get(integerPart - 1).getRecency()) * decimalPart;
                break;
            case RFMModel.F:
                quantilePoint = rfmModelList.get(integerPart - 1).getFrequency() +
                        (rfmModelList.get(integerPart).getFrequency() - rfmModelList.get(integerPart - 1).getFrequency()) * decimalPart;
                break;
            case RFMModel.M:
                quantilePoint = rfmModelList.get(integerPart - 1).getMonetary() +
                        (rfmModelList.get(integerPart).getMonetary() - rfmModelList.get(integerPart - 1).getMonetary()) * decimalPart;
                break;
        }

        return Double.valueOf(df.format(quantilePoint));
    }

}
