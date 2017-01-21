package io.sugo.pio.data.druid.tools;

import java.io.Serializable;


/**
 * A basic container class for a pair of objects.
 *
 * @author Sebastian Land
 */
public class Pair<T, K> implements Serializable {

    private static final long serialVersionUID = 1L;

    private T first;

    private K second;

    public Pair(T t, K k) {
        this.setFirst(t);
        this.setSecond(k);
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public K getSecond() {
        return second;
    }

    public void setSecond(K second) {
        this.second = second;
    }

    @Override
    public String toString() {
        String tString = (getFirst() == null) ? "null" : getFirst().toString();
        String kString = (getSecond() == null) ? "null" : getSecond().toString();
        return tString + " : " + kString;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((second == null) ? 0 : second.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Pair other = (Pair) obj;
        if (first == null) {
            if (other.first != null) {
                return false;
            }
        } else if (!first.equals(other.first)) {
            return false;
        }
        if (second == null) {
            if (other.second != null) {
                return false;
            }
        } else if (!second.equals(other.second)) {
            return false;
        }
        return true;
    }
}
