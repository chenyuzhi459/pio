package io.sugo.pio.recommend.algorithm;

public class FpAlgorithm extends AbstractAlgorithm {

    public FpAlgorithm() {
        setName("fp_query");
        setDescription("组合推荐");
        addArg("item_id", "产品id");
    }

    public static AbstractAlgorithm getInstance() {
        if (null == algorithm) {
            algorithm = new FpAlgorithm();
        }
        return algorithm;
    }
}
