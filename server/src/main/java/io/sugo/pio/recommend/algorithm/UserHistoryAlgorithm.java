package io.sugo.pio.recommend.algorithm;

public class UserHistoryAlgorithm extends AbstractAlgorithm {

    public UserHistoryAlgorithm() {
        setName("userHistory_query");
        setDescription("搜索推荐");
        addArg("item_id", "产品id");
        addArg("item_name", "产品名称");
    }

    public static AbstractAlgorithm getInstance() {
        if (null == algorithm) {
            algorithm = new UserHistoryAlgorithm();
        }
        return algorithm;
    }
}
