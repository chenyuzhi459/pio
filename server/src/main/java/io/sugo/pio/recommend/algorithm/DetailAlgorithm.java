package io.sugo.pio.recommend.algorithm;

public class DetailAlgorithm extends AbstractAlgorithm {

    public DetailAlgorithm() {
        setName("detail_query");
        setDescription("相似推荐");
        addArg("item_id", "产品id");
    }

    public static AbstractAlgorithm getInstance() {
        if (null == algorithm) {
            algorithm = new DetailAlgorithm();
        }
        return algorithm;
    }
}
