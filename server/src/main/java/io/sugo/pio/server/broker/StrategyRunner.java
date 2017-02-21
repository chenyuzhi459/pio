package io.sugo.pio.server.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.common.logger.Logger;
import io.sugo.pio.client.broker.EngineTaskClient;
import io.sugo.pio.data.fetcher.DataFetcher;
import io.sugo.pio.data.fetcher.DataFetcherConfig;
import io.sugo.pio.data.fetcher.RedisDataFetcher;
import io.sugo.pio.guice.ManageLifecycle;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.query.PredictionQuery;
import io.sugo.pio.recommend.algorithm.AbstractAlgorithm;
import io.sugo.pio.recommend.bean.RecStrategy;
import io.sugo.pio.server.broker.sort.ClickSorter;
import io.sugo.pio.server.broker.sort.PublishTimeSorter;
import io.sugo.pio.server.broker.sort.Sorter;
import io.sugo.pio.server.utils.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@ManageLifecycle
public class StrategyRunner {

    private static final Logger log = new Logger(StrategyRunner.class);

    private final EngineTaskClient client;
    private DataFetcher dataFetcher;
    private final Map<String, Sorter> sorterMap = new HashMap<>();
    private final ObjectMapper jsonMapper;

    @Inject
    public StrategyRunner(
            @Json ObjectMapper jsonMapper,
            EngineTaskClient client,
            DataFetcherConfig config
    ) {
        this.jsonMapper = jsonMapper;
        this.client = client;
        if (RedisDataFetcher.REDIS.equals(config.getType())) {
            Preconditions.checkArgument(StringUtil.isNotEmpty(config.getHostAndPorts()), "must specify parameter: pio.broker.data.fetcher.hostAndPorts");
            dataFetcher = new RedisDataFetcher(config.getHostAndPorts(), config.isClusterMode());
        }
        Preconditions.checkNotNull(dataFetcher, "must specify parameter: pio.broker.data.fetcher.type, default value is redis");
        initSorters();
    }

    private void initSorters() {
        sorterMap.put(ClickSorter.TYPE, new ClickSorter(dataFetcher));
        sorterMap.put(PublishTimeSorter.TYPE, new PublishTimeSorter(dataFetcher));
    }

    @LifecycleStart
    public void start() {
    }

    @LifecycleStop
    public void stop() {
    }

    public List<String> run(RecStrategy strategy) throws IOException {
        List<String> itemIds = dispatch(strategy);
        Preconditions.checkArgument(!itemIds.isEmpty(), "No data found");
        Sorter sorter = sorterMap.get(strategy.getOrderField());
        Preconditions.checkNotNull(sorter, "Field sorter is null");
        List<String> sortedItemIds = sorter.sort(itemIds, strategy.getAsc());
        return sortedItemIds;
    }

    private List<String> dispatch(RecStrategy strategy) throws IOException {
        Set<AbstractAlgorithm> algorithms = strategy.getAlgorithms();
        Map<String, Object> requestObject = new HashMap<>();
        Map<String, Object> requestData = new HashMap<>();
        List<String> itemIds = new ArrayList<>();
        for (AbstractAlgorithm algorithm : algorithms) {
            requestData.put("type", algorithm.getQueryType());
            requestData.put("num", strategy.getNum());
            requestData.putAll(strategy.getParmas());

            requestObject.put("type", PredictionQuery.TYPE);
            requestObject.put("queryObject", requestData);

            InputStream inputStream = client.runQuery(requestObject, algorithm.getType());
            PioResult pioResult = jsonMapper.readValue(inputStream, PioResult.class);
            itemIds.addAll(pioResult.getItems());
            requestData.clear();
            requestObject.clear();
        }
        return itemIds;
    }
}
