package io.sugo.pio.server.broker;

import com.google.inject.Inject;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.common.logger.Logger;
import io.sugo.pio.client.task.TaskServiceClient;
import io.sugo.pio.guice.ManageLifecycle;
import io.sugo.pio.recommend.algorithm.AbstractAlgorithm;
import io.sugo.pio.recommend.bean.RecStrategy;

import java.util.*;

@ManageLifecycle
public class StrategyRunner {

    private static final Logger log = new Logger(StrategyRunner.class);

    private final TaskServiceClient serviceClient;

    @Inject
    public StrategyRunner(
            TaskServiceClient serviceClient
    ) {
        this.serviceClient = serviceClient;
    }

    @LifecycleStart
    public void start() {
    }

    @LifecycleStop
    public void stop() {
    }

    public List<String> run(RecStrategy strategy) {
        Set<AbstractAlgorithm> algorithms = strategy.getAlgorithms();
        Map<String, Object> requestData = new HashMap<>();
        Set<String> itemSet = new HashSet<>();
        for(AbstractAlgorithm algorithm: algorithms){
            requestData.put("type", algorithm.getName());
            requestData.put("num", strategy.getNum());
            requestData.putAll(strategy.getParmas());
//            List<String> itemList = serviceClient.submitTask(requestData);
//            itemSet.addAll(itemList);
            requestData.clear();
        }
        return null;
    }
}
