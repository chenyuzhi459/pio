package io.sugo.pio.recommend.algorithm;

public class ALSAlgorithm extends AbstractAlgorithm {

    private ALSAlgorithm() {
        setName("als_query");
        setDescription("协同过滤推荐");
        addArg("user_id", "用户id");
    }

    public static AbstractAlgorithm getInstance() {
        if (null == algorithm) {
            algorithm = new ALSAlgorithm();
        }
        return algorithm;
    }
}
