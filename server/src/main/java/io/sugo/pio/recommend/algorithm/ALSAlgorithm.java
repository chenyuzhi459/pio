package io.sugo.pio.recommend.algorithm;

public class ALSAlgorithm extends AbstractAlgorithm {
    public static final String TYPE = "als";
    public static final String QUERY_TYPE = "als_query";
    protected static ALSAlgorithm algorithm;

    private ALSAlgorithm() {
        setType(TYPE);
        setQueryType(QUERY_TYPE);
        setDescription("协同过滤推荐");
        addArg("user_id", "用户id");
    }

    public static ALSAlgorithm getInstance() {
        if (null == algorithm) {
            algorithm = new ALSAlgorithm();
        }
        return algorithm;
    }
}
