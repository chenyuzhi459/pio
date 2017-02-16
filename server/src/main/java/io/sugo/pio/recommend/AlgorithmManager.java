package io.sugo.pio.recommend;

import io.sugo.pio.recommend.algorithm.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AlgorithmManager {
    private static Map<String, AbstractAlgorithm> algorithmMap = new HashMap<>();

    static {
        algorithmMap.put(ALSAlgorithm.getInstance().getName(), ALSAlgorithm.getInstance());
        algorithmMap.put(DetailAlgorithm.getInstance().getName(), DetailAlgorithm.getInstance());
        algorithmMap.put(FpAlgorithm.getInstance().getName(), FpAlgorithm.getInstance());
        algorithmMap.put(PopAlgorithm.getInstance().getName(), PopAlgorithm.getInstance());
        algorithmMap.put(SearchAlgorithm.getInstance().getName(), SearchAlgorithm.getInstance());
        algorithmMap.put(UserHistoryAlgorithm.getInstance().getName(), UserHistoryAlgorithm.getInstance());
    }

    public static AbstractAlgorithm get(String name) {
        return algorithmMap.get(name);
    }

    public static Collection<AbstractAlgorithm> getAll(){
        return algorithmMap.values();
    }
}
