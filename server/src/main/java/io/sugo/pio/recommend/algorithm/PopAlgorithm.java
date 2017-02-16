package io.sugo.pio.recommend.algorithm;

public class PopAlgorithm extends AbstractAlgorithm {

    public PopAlgorithm() {
        setName("pop_query");
        setDescription("流行推荐");
        addArg("category", "类别");
    }

    public static AbstractAlgorithm getInstance() {
        if (null == algorithm) {
            algorithm = new PopAlgorithm();
        }
        return algorithm;
    }
}
