package io.sugo.pio.spark.transfer.model;

import java.util.Set;

/**
 */
public class ContainsSplitConditionTO extends SplitConditionTO {
    private Set<String> categories;

    public ContainsSplitConditionTO(String attributeName, Set<String> categories) {
        super(attributeName);
        this.categories = categories;
    }

    public Set<String> getCategories() {
        return this.categories;
    }

    public void setCategories(Set<String> categories) {
        this.categories = categories;
    }
}

