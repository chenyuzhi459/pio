package io.sugo.pio.server.broker.sort;

import io.sugo.pio.server.broker.Item;

import java.util.Comparator;

public class ClickComparator implements Comparator<Item> {

    private boolean asc;

    public ClickComparator(boolean asc) {
        this.asc = asc;
    }

    @Override
    public int compare(Item o1, Item o2) {
        if (asc) {
            return o1.getClick().compareTo(o2.getClick());
        } else {
            return o2.getClick().compareTo(o1.getClick());
        }
    }
}
