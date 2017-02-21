package io.sugo.pio.recommend.algorithm;

public class SearchAlgorithm extends AbstractAlgorithm {
    public static final String TYPE = "itemSearch";
    public static final String QUERY_TYPE = "search_query";
    protected static SearchAlgorithm algorithm;

    public SearchAlgorithm() {
        setType(TYPE);
        setQueryType(QUERY_TYPE);
        setDescription("搜索匹配推荐");
        addArg("item_name", "产品名称");
    }

    public static SearchAlgorithm getInstance() {
        if (null == algorithm) {
            algorithm = new SearchAlgorithm();
        }
        return algorithm;
    }
}
