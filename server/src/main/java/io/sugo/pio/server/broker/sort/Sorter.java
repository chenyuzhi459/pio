package io.sugo.pio.server.broker.sort;

import io.sugo.pio.server.broker.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class Sorter {

    public List<String> sort(List<String> itemIdSet, boolean asc) {
        List<Item> items = fetchSortingData(itemIdSet);
        Collections.sort(items, getComparator(asc));
        List<String> sortedItemId = new ArrayList<>(items.size());
        for (Item item : items) {
            sortedItemId.add(item.getItemId());
        }
        return sortedItemId;
    }

    protected abstract Comparator getComparator(boolean asc);

    protected abstract List<Item> fetchSortingData(List<String> itemIdSet);

}
