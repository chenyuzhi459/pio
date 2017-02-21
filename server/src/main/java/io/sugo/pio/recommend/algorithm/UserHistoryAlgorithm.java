package io.sugo.pio.recommend.algorithm;

public class UserHistoryAlgorithm extends AbstractAlgorithm {
    public static final String TYPE = "userSearch";
    public static final String QUERY_TYPE = "userHistory_query";
    protected static UserHistoryAlgorithm algorithm;

    public UserHistoryAlgorithm() {
        setType(TYPE);
        setQueryType(QUERY_TYPE);
        setDescription("搜索推荐");
        addArg("item_id", "产品id");
        addArg("item_name", "产品名称");
    }

    public static UserHistoryAlgorithm getInstance() {
        if (null == algorithm) {
            algorithm = new UserHistoryAlgorithm();
        }
        return algorithm;
    }
}
