package io.sugo.pio.recommend;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.common.logger.Logger;
import io.sugo.pio.client.broker.BrokerServiceClient;
import io.sugo.pio.guice.ManageLifecycle;
import io.sugo.pio.metadata.MetadataRecInstanceManager;
import io.sugo.pio.recommend.algorithm.AbstractAlgorithm;
import io.sugo.pio.recommend.bean.RecInstance;
import io.sugo.pio.recommend.bean.RecStrategy;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ManageLifecycle
public class RecommendProxy {

    private static final Logger log = new Logger(RecommendProxy.class);

    private final MetadataRecInstanceManager recInstanceManager;
    private final BrokerServiceClient brokerServiceClient;


    @Inject
    public RecommendProxy(
            BrokerServiceClient brokerServiceClient,
            MetadataRecInstanceManager recInstanceManager
    ) {
        this.brokerServiceClient = brokerServiceClient;
        this.recInstanceManager = recInstanceManager;
    }

    @LifecycleStart
    public void start() {
    }

    @LifecycleStop
    public void stop() {
    }

    public RecInstance getRecInstance(String id) {
        RecInstance entry = recInstanceManager.get(id);
        return entry;
    }

    public List<String> recommend(String id, HttpServletRequest req) {
        RecInstance entry = getRecInstance(id);
        Preconditions.checkNotNull(entry, "No Recommend found with id:" + id);
        Preconditions.checkArgument(entry.getEnabled(), "Recommend with id:" + id + " is disabled");

        Map<String, RecStrategy> strategies = entry.getRecStrategys();
        RecStrategy strategy = StrategySelector.select(strategies, req.getSession().getId());
        strategy.setNum(entry.getNum());
        checkAndParseParameters(strategy, req);
        brokerServiceClient.runQuery(strategy);

        return null;
    }

    private void checkAndParseParameters(RecStrategy strategy, HttpServletRequest req) {
        Set<AbstractAlgorithm> algorithms = strategy.getAlgorithms();
        for (AbstractAlgorithm algorithm : algorithms) {
            Set<String> argNames = algorithm.getArgs().keySet();
            for (String argName : argNames) {
                String argVal = req.getParameter(argName);
                Preconditions.checkNotNull(argVal, "Must specify arg:" + argName);
                strategy.addParams(argName, argVal);
            }
        }
    }
}
