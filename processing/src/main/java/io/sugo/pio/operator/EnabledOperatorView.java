package io.sugo.pio.operator;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;

/**
 */
public class EnabledOperatorView extends AbstractList<Operator> {

    private Collection<Operator> base;

    public EnabledOperatorView(Collection<Operator> base) {
        super();
        this.base = base;
    }

    @Override
    public Iterator<Operator> iterator() {
        return new Iterator<Operator>() {

            private Operator next;
            private Iterator<Operator> baseIterator = base.iterator();

            @Override
            public boolean hasNext() {
                if (next != null) {
                    return true;
                }
                while (baseIterator.hasNext()) {
                    next = baseIterator.next();
                    if (next.isEnabled()) {
                        return true;
                    }
                }
                next = null;
                return false;
            }

            @Override
            public Operator next() {
                hasNext();
                Operator result = next;
                next = null;
                return result;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Unmodifyable view!");
            }

        };
    }

    @Override
    public int size() {
        int size = 0;
        Iterator<Operator> i = iterator();
        while (i.hasNext()) {
            i.next();
            size++;
        }
        return size;
    }

    @Override
    public Operator get(int index) {
        int n = 0;
        Iterator<Operator> i = iterator();
        while (i.hasNext()) {
            Operator next = i.next();
            if (n == index) {
                return next;
            }
            n++;
        }
        return null;
    }
}