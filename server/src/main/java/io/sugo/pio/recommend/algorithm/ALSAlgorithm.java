package io.sugo.pio.recommend.algorithm;

public class ALSAlgorithm extends AbstractAlgorithm {
    public static final String TYPE = "als_query";
    protected static ALSAlgorithm algorithm;

    private ALSAlgorithm() {
        setName(TYPE);
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
