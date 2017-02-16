package io.sugo.pio.recommend.algorithm;

public class SearchAlgorithm extends AbstractAlgorithm {

    public SearchAlgorithm() {
        setName("search_query");
        setDescription("搜索匹配推荐");
        addArg("item_name", "产品名称");
    }

    public static AbstractAlgorithm getInstance() {
        if (null == algorithm) {
            algorithm = new SearchAlgorithm();
        }
        return algorithm;
    }
}
