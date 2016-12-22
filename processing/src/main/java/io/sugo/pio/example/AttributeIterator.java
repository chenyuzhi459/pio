package io.sugo.pio.example;

import java.util.Iterator;

/**
 */
public class AttributeIterator implements Iterator<Attribute> {
    private Iterator<AttributeRole> parent;

    private int type = Attributes.REGULAR;

    private Attribute current = null;

    private boolean hasNextInvoked = false;

    private AttributeRole currentRole = null;

    public AttributeIterator(Iterator<AttributeRole> parent, int type) {
        this.parent = parent;
        this.type = type;
    }

    @Override
    public boolean hasNext() {
        this.hasNextInvoked = true;
        if (!parent.hasNext() && currentRole == null) {
            current = null;
            return false;
        } else {
            AttributeRole role;
            if (currentRole == null) {
                role = parent.next();
            } else {
                role = currentRole;
            }
            switch (type) {
                case Attributes.REGULAR:
                    if (!role.isSpecial()) {
                        current = role.getAttribute();
                        currentRole = role;
                        return true;
                    } else {
                        return hasNext();
                    }
                case Attributes.SPECIAL:
                    if (role.isSpecial()) {
                        current = role.getAttribute();
                        currentRole = role;
                        return true;
                    } else {
                        return hasNext();
                    }
                case Attributes.ALL:
                    current = role.getAttribute();
                    currentRole = role;
                    return true;
                default:
                    current = null;
                    return false;
            }
        }
    }

    @Override
    public Attribute next() {
        if (!this.hasNextInvoked) {
            hasNext();
        }
        this.hasNextInvoked = false;
        this.currentRole = null;
        return current;
    }

    @Override
    public void remove() {
        parent.remove();
        this.currentRole = null;
        this.hasNextInvoked = false;
    }
}
