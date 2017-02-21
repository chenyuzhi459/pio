package io.sugo.pio.server.broker.sort;

import io.sugo.pio.data.fetcher.DataFetcher;
import io.sugo.pio.server.broker.Item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClickSorter extends Sorter {
    public static final String TYPE = "click";
    private static final ClickComparator ascComparator = new ClickComparator(true);
    private static final ClickComparator descComparator = new ClickComparator(false);
    private DataFetcher dataFetcher;

    public ClickSorter(DataFetcher dataFetcher) {
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
        String value;
        for (int i = 0; i < values.length; i++) {
            Item item = new Item(itemIdSet.get(i));
            value = values[i];
            if (value == null) {
                item.setClick(0L);
            } else {
                item.setClick(Long.valueOf(value));
            }
            items.add(item);
        }
        return items;
    }
}
