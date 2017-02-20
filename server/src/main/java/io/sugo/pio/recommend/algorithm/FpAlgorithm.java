package io.sugo.pio.recommend.algorithm;

public class FpAlgorithm extends AbstractAlgorithm {
    public static final String TYPE = "fp_query";
    protected static FpAlgorithm algorithm;

    public FpAlgorithm() {
        setName(TYPE);
        setDescription("组合推荐");
        addArg("item_id", "产品id");
    }

    public static FpAlgorithm getInstance() {
        if (null == algorithm) {
            algorithm = new FpAlgorithm();
        }
        return algorithm;
    }
}
