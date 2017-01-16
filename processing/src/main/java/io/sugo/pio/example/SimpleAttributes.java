package io.sugo.pio.example;

import java.util.*;

/**
 */
public class SimpleAttributes extends AbstractAttributes {
    private final List<AttributeRole> attributes;

    private transient Map<String, AttributeRole> nameToAttributeRoleMap = new HashMap<>();
    private transient Map<String, AttributeRole> specialNameToAttributeRoleMap = new HashMap<>();

    public SimpleAttributes() {
        this.attributes = Collections.synchronizedList(new ArrayList<AttributeRole>());
    }

    private SimpleAttributes(SimpleAttributes attributes) {
        this.attributes = Collections.synchronizedList(new ArrayList<AttributeRole>(attributes.allSize()));
        for (AttributeRole role : attributes.attributes) {
            register((AttributeRole) role.clone(), false);
        }
    }

    @Override
    public Object clone() {
        return new SimpleAttributes(this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SimpleAttributes)) {
            return false;
        }
        SimpleAttributes other = (SimpleAttributes) o;
        return attributes.equals(other.attributes);
    }

    @Override
    public int hashCode() {
        return attributes.hashCode();
    }

    @Override
    public Iterator<AttributeRole> allAttributeRoles() {
        final Iterator<AttributeRole> i = attributes.iterator();
        return new Iterator<AttributeRole>() {

            private AttributeRole current;

            @Override
            public boolean hasNext() {
                return i.hasNext();
            }

            @Override
            public AttributeRole next() {
                current = i.next();
                return current;
            }

            @Override
            public void remove() {
                i.remove();
                unregister(current, true);
            }
        };
    }

    @Override
    public void add(AttributeRole attributeRole) {
        register(attributeRole, false);
    }

    @Override
    public boolean remove(AttributeRole attributeRole) {
        return unregister(attributeRole, false);
    }

    @Override
    public AttributeRole findRoleByName(String name, boolean caseSensitive) {
        if (caseSensitive) {
            return nameToAttributeRoleMap.get(name);
        } else {
            String lowerSearchTerm = name.toLowerCase();
            for (Map.Entry<String, AttributeRole> entry : nameToAttributeRoleMap.entrySet()) {
                if (lowerSearchTerm.equals(entry.getKey().toLowerCase())) {
                    return entry.getValue();
                }
            }
            return null;
        }
    }
    /**
     * @param onlyMaps
     *            add only to maps, not to list.
     */
    private void register(AttributeRole attributeRole, boolean onlyMaps) {
        String name = attributeRole.getAttribute().getName();
        if (nameToAttributeRoleMap.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate attribute name: " + name);
        }
        String specialName = attributeRole.getSpecialName();
        if (specialName != null) {
            if (specialNameToAttributeRoleMap.containsKey(specialName)) {
                throw new IllegalArgumentException("Duplicate attribute role: " + specialName);
            }
        }
        this.nameToAttributeRoleMap.put(name, attributeRole);
        if (specialName != null) {
            this.specialNameToAttributeRoleMap.put(specialName, attributeRole);
        }
        if (!onlyMaps) {
            this.attributes.add(attributeRole);
        }
        attributeRole.addOwner(this);
        attributeRole.getAttribute().addOwner(this);
    }

    /**
     *
     * @param onlyMap
     *            if true, removes the attribute only from the maps, but not from the list. this is
     *            useful for the iterator, which has already removed the attribute from the list.
     */
    private boolean unregister(AttributeRole attributeRole, boolean onlyMap) {
        if (!nameToAttributeRoleMap.containsKey(attributeRole.getAttribute().getName())) {
            return false;
        }
        this.nameToAttributeRoleMap.remove(attributeRole.getAttribute().getName());
        if (attributeRole.getSpecialName() != null) {
            this.specialNameToAttributeRoleMap.remove(attributeRole.getSpecialName());
        }
        if (!onlyMap) {
            this.attributes.remove(attributeRole);
        }
        attributeRole.removeOwner(this);
        attributeRole.getAttribute().removeOwner(this);
        return true;
    }

    @Override
    public AttributeRole findRoleBySpecialName(String specialName, boolean caseSensitive) {
        if (caseSensitive) {
            return specialNameToAttributeRoleMap.get(specialName);
        } else {
            String lowerSearchTerm = specialName.toLowerCase();
            for (Map.Entry<String, AttributeRole> entry : specialNameToAttributeRoleMap.entrySet()) {
                if (lowerSearchTerm.equals(entry.getKey().toLowerCase())) {
                    return entry.getValue();
                }
            }
            return null;
        }
    }

    @Override
    public void rename(AttributeRole attributeRole, String newSpecialName) {
        if (attributeRole.getSpecialName() != null) {
            AttributeRole role = specialNameToAttributeRoleMap.get(attributeRole.getSpecialName());
            if (role == null) {
                throw new NoSuchElementException(
                        "Cannot rename attribute role. No such attribute role: " + attributeRole.getSpecialName());
            }
            if (role != attributeRole) {
                throw new RuntimeException("Broken attribute role map.");
            }
        }
        specialNameToAttributeRoleMap.remove(attributeRole.getSpecialName());
        if (newSpecialName != null) {
            specialNameToAttributeRoleMap.put(newSpecialName, attributeRole);
        }

    }

    @Override
    public void rename(Attribute attribute, String newName) {
        if (nameToAttributeRoleMap.containsKey(newName)) {
            throw new IllegalArgumentException("Cannot rename attribute. Duplicate name: " + newName);
        }
        AttributeRole role = nameToAttributeRoleMap.get(attribute.getName());
        if (role == null) {
            throw new NoSuchElementException("Cannot rename attribute. No such attribute: " + attribute.getName());
        }
        if (role.getAttribute() != attribute) {
            // this cannot happen
            throw new RuntimeException("Broken attribute map.");
        }
        nameToAttributeRoleMap.remove(role.getAttribute().getName());
        nameToAttributeRoleMap.put(newName, role);
    }

    @Override
    public int size() {
        return nameToAttributeRoleMap.size() - specialNameToAttributeRoleMap.size();
    }

}
