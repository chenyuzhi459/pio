package io.sugo.pio.recommend.algorithm;

public class DetailAlgorithm extends AbstractAlgorithm {
    public static final String TYPE = "detail_query";
    protected static DetailAlgorithm algorithm;

    public DetailAlgorithm() {
        setName(TYPE);
        setDescription("相似推荐");
        addArg("item_id", "产品id");
    }

    public static DetailAlgorithm getInstance() {
        if (null == algorithm) {
            algorithm = new DetailAlgorithm();
        }
        return algorithm;
    }
}
