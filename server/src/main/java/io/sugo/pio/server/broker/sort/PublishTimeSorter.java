package io.sugo.pio.server.broker.sort;

import io.sugo.pio.data.fetcher.DataFetcher;
import io.sugo.pio.server.broker.Item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PublishTimeSorter extends Sorter {
    public static final String TYPE = "publish_time";
    private final Comparator ascComparator = new PublishTimeComparator(true);
    private final Comparator descComparator = new PublishTimeComparator(false);
    private DataFetcher dataFetcher;

    public PublishTimeSorter(DataFetcher dataFetcher) {
        this.dataFetcher = dataFetcher;
    }

    @Override
    protected Comparator getComparator(boolean asc) {
        if (asc) {
            return ascComparator;
        } else {
            return descComparator;
        }
    }

    @Override
    protected List<Item> fetchSortingData(List<String> itemIdSet) {
        String[] values = dataFetcher.fetchData(itemIdSet, TYPE);
        List<Item> items = new ArrayList<>(values.length);
        for (int i = 0; i < values.length; i++) {
            Item item = new Item(itemIdSet.get(i));
            item.setPublishTime(Long.valueOf(values[i]));
        }
        return items;
    }
}
