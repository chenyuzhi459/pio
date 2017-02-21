package io.sugo.pio.recommend;

import io.sugo.pio.recommend.algorithm.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AlgorithmManager {
    private static Map<String, AbstractAlgorithm> algorithmMap = new HashMap<>();

    static {
        algorithmMap.put(ALSAlgorithm.getInstance().getQueryType(), ALSAlgorithm.getInstance());
        algorithmMap.put(DetailAlgorithm.getInstance().getQueryType(), DetailAlgorithm.getInstance());
        algorithmMap.put(FpAlgorithm.getInstance().getQueryType(), FpAlgorithm.getInstance());
        algorithmMap.put(PopAlgorithm.getInstance().getQueryType(), PopAlgorithm.getInstance());
        algorithmMap.put(SearchAlgorithm.getInstance().getQueryType(), SearchAlgorithm.getInstance());
        algorithmMap.put(UserHistoryAlgorithm.getInstance().getQueryType(), UserHistoryAlgorithm.getInstance());
    }

    public static AbstractAlgorithm get(String name) {
        return algorithmMap.get(name);
    }

    public static Collection<AbstractAlgorithm> getAll(){
        return algorithmMap.values();
    }
}
