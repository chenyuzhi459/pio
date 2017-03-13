package io.sugo.pio.operator.learner.associations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.Attribute;


/**
 * This is an {@link Item} based on boolean attributes.
 */
public class BooleanAttributeItem implements Item {

    private static final long serialVersionUID = -7963677912091349984L;

    @JsonProperty
    private int frequency = 0;

    @JsonProperty
    private String name;

    public BooleanAttributeItem(Attribute item) {
        this.name = item.getName();
    }

    @Override
    public int getFrequency() {
        return this.frequency;
    }

    @Override
    public void increaseFrequency() {
        this.frequency++;
    }

    public void increaseFrequency(double value) {
        this.frequency += value;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof BooleanAttributeItem)) {
            return false;
        }
        BooleanAttributeItem o = (BooleanAttributeItem) other;
        return (this.name.equals(o.name)) && (this.frequency == o.frequency);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() ^ Double.valueOf(this.frequency).hashCode();
    }

    @Override
    public int compareTo(Item arg0) {
        Item comparer = arg0;
        // Collections.sort generates ascending order. Descending needed,
        // therefore invert return values!
        if (comparer.getFrequency() == this.getFrequency()) {
            return (-1 * this.name.compareTo(arg0.toString()));
        } else if (comparer.getFrequency() < this.getFrequency()) {
            return -1;
        } else {
            return 1;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public void increaseFrequency(int value) {
        frequency += value;
    }
}
