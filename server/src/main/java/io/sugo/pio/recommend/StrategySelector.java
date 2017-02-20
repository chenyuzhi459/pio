package io.sugo.pio.recommend;

import io.sugo.pio.recommend.bean.RecStrategy;
import io.sugo.pio.server.utils.MurmurhashUtil;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class StrategySelector {

    public static RecStrategy select(Map<String, RecStrategy> strategies, String sessionId) {
        Collection<RecStrategy> strategyList = strategies.values();
        if (strategyList.size() == 1) {
            return strategyList.iterator().next();
        }
        int hashIndex = MurmurhashUtil.hash(sessionId) % 100;
        Iterator<RecStrategy> iterator = strategies.values().iterator();
        RecStrategy strategy = null;
        while (iterator.hasNext()) {
            strategy = iterator.next();
            if (iterator.next().match(hashIndex)) {
                return strategy;
            }
        }
        return strategy;
    }
}
