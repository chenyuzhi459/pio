package io.sugo.pio.tools.math;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.operator.ResultObjectAdapter;
import io.sugo.pio.tools.Tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Handles several averagables.
 *
 */
public abstract class AverageVector extends ResultObjectAdapter implements Comparable, Cloneable {

    private static final long serialVersionUID = 6207859713603581755L;

    private List<Averagable> averagesList = new ArrayList<Averagable>();

    @Override
    public abstract Object clone() throws CloneNotSupportedException;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AverageVector)) {
            return false;
        }
        AverageVector v = (AverageVector) o;
        return averagesList.equals(v.averagesList);
    }

    @Override
    public int hashCode() {
        return this.averagesList.hashCode();
    }

    /** Returns the number of averages in the list. */
    public int size() {
        return averagesList.size();
    }

    /** Adds an {@link Averagable} to the list of criteria. */
    public void addAveragable(Averagable avg) {
        averagesList.add(avg);
    }

    /** Removes an {@link Averagable} from the list of criteria. */
    public void removeAveragable(Averagable avg) {
        averagesList.remove(avg);
    }

    /** Returns the Averagable by index. */
    public Averagable getAveragable(int index) {
        return averagesList.get(index);
    }

    @JsonProperty
    public List<Averagable> getAveragable() {
        return averagesList;
    }

    /** Returns the Averagable by name. */
    public Averagable getAveragable(String name) {
        Iterator<Averagable> i = averagesList.iterator();
        while (i.hasNext()) {
            Averagable a = i.next();
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }

    /** Returns the number of averagables in this vector. */
    public int getSize() {
        return averagesList.size();
    }

    @Override
    public String toResultString() {
        StringBuffer result = new StringBuffer(getName());
        result.append(":");
        result.append(Tools.getLineSeparator());
        Iterator<Averagable> i = averagesList.iterator();
        while (i.hasNext()) {
            result.append(i.next().toResultString());
            result.append(Tools.getLineSeparator());
        }
        return result.toString();
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer("AverageVector [");
        for (int i = 0; i < size(); i++) {
            Averagable avg = getAveragable(i);
            if (i > 0) {
                result.append(", ");
            }
            result.append(avg);
        }
        result.append("]");
        return result.toString();
    }

    public void buildAverages(AverageVector av) {
        if (this.size() != av.size()) {
            throw new IllegalArgumentException("Performance vectors have different size!");
        }
        for (int i = 0; i < size(); i++) {
            this.getAveragable(i).buildAverage(av.getAveragable(i));
        }
    }
}
