package io.sugo.pio.recommend.algorithm;

public class PopAlgorithm extends AbstractAlgorithm {
    public static final String TYPE = "pop";
    public static final String QUERY_TYPE = "pop_query";
    protected static PopAlgorithm algorithm;

    public PopAlgorithm() {
        setType(TYPE);
        setQueryType(QUERY_TYPE);
        setDescription("流行推荐");
        addArg("category", "类别");
    }

    public static PopAlgorithm getInstance() {
        if (null == algorithm) {
            algorithm = new PopAlgorithm();
        }
        return algorithm;
    }
}
