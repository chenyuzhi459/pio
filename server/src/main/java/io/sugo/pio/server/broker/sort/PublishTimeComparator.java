package io.sugo.pio.server.broker.sort;

import io.sugo.pio.server.broker.Item;

import java.util.Comparator;

public class PublishTimeComparator implements Comparator<Item> {

    private boolean asc;

    public PublishTimeComparator(boolean asc) {
        this.asc = asc;
    }

    @Override
    public int compare(Item o1, Item o2) {
        if (asc) {
            return o1.getPublishTime().compareTo(o2.getPublishTime());
        } else {
            return o2.getPublishTime().compareTo(o1.getPublishTime());
        }
    }
}
