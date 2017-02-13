package io.sugo.pio.recommend;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.common.logger.Logger;
import io.sugo.pio.client.broker.BrokerServiceClient;
import io.sugo.pio.guice.ManageLifecycle;
import io.sugo.pio.metadata.MetadataRecInstanceManager;
import io.sugo.pio.recommend.bean.RecInstance;
import io.sugo.pio.recommend.bean.RecInstanceCriteria;
import io.sugo.pio.recommend.bean.RecStrategy;
import io.sugo.pio.server.utils.StringUtil;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ManageLifecycle
public class RecommendManager {

    private static final Logger log = new Logger(RecommendManager.class);

    private final MetadataRecInstanceManager recInstanceManager;
    private final BrokerServiceClient brokerServiceClient;

    @Inject
    public RecommendManager(
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

    public void create(RecInstance recInstance) {
        recInstance.setUpdateTime(recInstance.getCreateTime());
        recInstanceManager.insert(recInstance);
    }

    public RecInstance update(RecInstance recInstance) {
        RecInstance entry = getRecInstance(recInstance.getId());
        if (entry != null) {
            if (StringUtil.isNotEmpty(recInstance.getName())) {
                entry.setName(recInstance.getName());
            }
            if (recInstance.getNum() != null) {
                entry.setNum(recInstance.getNum());
            }
            if (recInstance.getEnabled() != null) {
                entry.setEnabled(recInstance.getEnabled());
            }
            entry.setUpdateTime(new DateTime());
            recInstanceManager.update(entry);
        }
        return entry;
    }

    public RecInstance enable(String id, Boolean enabled) {
        RecInstance entry = getRecInstance(id);
        if (entry != null) {
            entry.setEnabled(enabled);
            entry.setUpdateTime(new DateTime());
            recInstanceManager.update(entry);
        }
        return entry;
    }

    public RecInstance delete(String id) {
        RecInstance entry = getRecInstance(id);
        if (entry != null) {
            recInstanceManager.delete(id);
        }
        return entry;
    }

    public RecInstance getRecInstance(String id) {
        RecInstance entry = recInstanceManager.get(id);
        return entry;
    }

    public List<RecInstance> getAll(RecInstanceCriteria criteria) {
        List<RecInstance> enties = recInstanceManager.getAll(criteria);
        if (enties == null) {
            enties = new ArrayList<>();
        }
        return enties;
    }

    public void addStrategy(String recId, RecStrategy recStrategy) {
        RecInstance entry = getRecInstance(recId);
        Preconditions.checkNotNull(entry, "No Recommend Instance found with id:" + recId);
        entry.setUpdateTime(new DateTime());
        entry.addRecStrategy(recStrategy);
        recInstanceManager.update(entry);
    }

    public void updateStrategy(String recId, String strategyId, RecStrategy recStrategy) {
        RecInstance entry = getRecInstance(recId);
        Preconditions.checkNotNull(entry, "No Recommend Instance found with id:" + recId);
        RecStrategy strategy = entry.getRecStrategy(strategyId);
        Preconditions.checkNotNull(strategy, "No Recommend Strategy found with id:" + strategyId);
        if (StringUtil.isNotEmpty(recStrategy.getName())) {
            strategy.setName(recStrategy.getName());
        }
        if (StringUtil.isNotEmpty(recStrategy.getOrderField())) {
            strategy.setOrderField(recStrategy.getOrderField());
        }
        if (recStrategy.getAsc() != null) {
            strategy.setAsc(recStrategy.getAsc());
        }
        if (recStrategy.getTypes() != null) {
            strategy.setTypes(recStrategy.getTypes());
        }
        entry.setUpdateTime(new DateTime());
        recInstanceManager.update(entry);
    }

    public RecInstance adjustPercent(String recId, Map<String, Integer> percents) {
        RecInstance entry = getRecInstance(recId);
        Preconditions.checkNotNull(entry, "No Recommend Instance found with id:" + recId);
        int totalPercent = 0;
        for (Map.Entry<String, Integer> item : percents.entrySet()) {
            RecStrategy strategy = entry.getRecStrategy(item.getKey());
            Preconditions.checkNotNull(strategy, "No Recommend Strategy found with id:" + item.getKey());
            strategy.setPercent(item.getValue(), totalPercent);
            totalPercent += item.getValue();
        }
        Preconditions.checkArgument(totalPercent == 100, "sum value of all percentage must be 100%");
        recInstanceManager.update(entry);
        return entry;
    }

    public RecStrategy deleteStrategy(String recId, String strategyId) {
        RecInstance entry = getRecInstance(recId);
        Preconditions.checkNotNull(entry, "No Recommend Instance found with id:" + recId);
        RecStrategy strategy = entry.deleteStrategy(strategyId);
        Preconditions.checkNotNull(strategy, "No Recommend Strategy found with id:" + strategyId);
        recInstanceManager.update(entry);
        return strategy;
    }

    public List<String> recommend(String id, String sessionId) {
        RecInstance entry = getRecInstance(id);
        Preconditions.checkNotNull(entry, "No Recommend found with id:" + id);

        Map<String, RecStrategy> strategies = entry.getRecStrategys();
        RecStrategy strategy = StrategySelector.select(strategies, sessionId);


        return null;
    }
}
