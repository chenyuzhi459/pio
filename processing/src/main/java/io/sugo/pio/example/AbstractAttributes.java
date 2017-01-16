package io.sugo.pio.example;

import java.util.Iterator;

/**
 */
public abstract class AbstractAttributes implements Attributes {
    @Override
    public abstract Object clone();

    @Override
    public Iterator<Attribute> iterator() {
        return new AttributeIterator(allAttributeRoles(), REGULAR);
    }

    @Override
    public Iterator<Attribute> allAttributes() {
        return new AttributeIterator(allAttributeRoles(), ALL);
    }

    @Override
    public int allSize() {
        return calculateSize(allAttributes());
    }

    @Override
    public int size() {
        return calculateSize(iterator());
    }

    private int calculateSize(Iterator i) {
        int counter = 0;
        while (i.hasNext()) {
            i.next();
            counter++;
        }
        return counter;
    }

    @Override
    public Attribute getSpecial(String name) {
        AttributeRole role = findRoleBySpecialName(name);
        if (role == null) {
            return null;
        } else {
            return role.getAttribute();
        }
    }

    @Override
    public AttributeRole findRoleBySpecialName(String specialName) {
        return findRoleBySpecialName(specialName, true);
    }

    @Override
    public AttributeRole findRoleByName(String name) {
        return findRoleByName(name, true);
    }

    @Override
    public Attribute getLabel() {
        return getSpecial(LABEL_NAME);
    }

    @Override
    public boolean remove(Attribute attribute) {
        AttributeRole role = getRole(attribute);
        if (role != null) {
            return remove(role);
        } else {
            return false;
        }
    }

    @Override
    public AttributeRole getRole(Attribute attribute) {
        return getRole(attribute.getName());
    }

    @Override
    public AttributeRole getRole(String name) {
        return findAttributeRole(name);
    }

    private AttributeRole findAttributeRole(String name) {
        AttributeRole role = findRoleByName(name);
        if (role != null) {
            return role;
        } else {
            return findRoleBySpecialName(name);
        }
    }
}
