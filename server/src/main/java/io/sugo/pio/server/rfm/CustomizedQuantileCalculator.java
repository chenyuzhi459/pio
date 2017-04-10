package io.sugo.pio.server.rfm;

import java.util.List;

/**
 */
public class CustomizedQuantileCalculator extends AbstractQuantileCalculator {

    private double[] rq;

    private double[] fq;

    private double[] mq;

    public CustomizedQuantileCalculator(List<RFMModel> rfmModelList, double[] rq, double[] fq, double[] mq) {
        super(rfmModelList);
        this.rq = rq;
        this.fq = fq;
        this.mq = mq;
    }

    @Override
    public void initModel() {
        quantileModel = new QuantileModel(rq.length+1, fq.length+1, mq.length+1);
        quantileModel.setRq(rq);
        quantileModel.setFq(fq);
        quantileModel.setMq(mq);
    }

}
