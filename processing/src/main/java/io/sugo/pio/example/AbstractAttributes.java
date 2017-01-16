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
    public boolean contains(Attribute attribute) {
        return findAttributeRole(attribute.getName()) != null;
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
    public boolean remove(Attribute attribute) {
        AttributeRole role = getRole(attribute);
        if (role != null) {
            return remove(role);
        } else {
            return false;
        }
    }

    @Override
    public Attribute get(String name) {
        return get(name, true);
    }

    @Override
    public Attribute get(String name, boolean caseSensitive) {
        AttributeRole result = findRoleByName(name, caseSensitive);
        if (result == null) {
            result = findRoleBySpecialName(name, caseSensitive);
        }
        if (result != null) {
            return result.getAttribute();
        } else {
            return null;
        }
    }

    @Override
    public AttributeRole findRoleByName(String name) {
        return findRoleByName(name, true);
    }

    @Override
    public AttributeRole findRoleBySpecialName(String specialName) {
        return findRoleBySpecialName(specialName, true);
    }

    @Override
    public Attribute getRegular(String name) {
        AttributeRole role = findRoleByName(name);
        if (role != null) {
            if (!role.isSpecial()) {
                return role.getAttribute();
            } else {
                return null;
            }
        } else {
            return null;
        }
        // return findAttribute(name, iterator());
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
    public AttributeRole getRole(Attribute attribute) {
        return getRole(attribute.getName());
    }

    @Override
    public AttributeRole getRole(String name) {
        return findAttributeRole(name);
    }

    @Override
    public Attribute getLabel() {
        return getSpecial(LABEL_NAME);
    }

    public void setLabel(Attribute label) {
        setSpecialAttribute(label, LABEL_NAME);
    }

    @Override
    public Attribute getPredictedLabel() {
        return getSpecial(PREDICTION_NAME);
    }

    @Override
    public Attribute getConfidence(String classLabel) {
        return getSpecial(CONFIDENCE_NAME + "_" + classLabel);
    }

    @Override
    public void setPredictedLabel(Attribute predictedLabel) {
        setSpecialAttribute(predictedLabel, PREDICTION_NAME);
    }

    @Override
    public Attribute getId() {
        return getSpecial(ID_NAME);
    }

    @Override
    public void setId(Attribute id) {
        setSpecialAttribute(id, ID_NAME);
    }

    @Override
    public Attribute getWeight() {
        return getSpecial(WEIGHT_NAME);
    }

    @Override
    public void setWeight(Attribute weight) {
        setSpecialAttribute(weight, WEIGHT_NAME);
    }

    @Override
    public Attribute getCluster() {
        return getSpecial(CLUSTER_NAME);
    }

    @Override
    public void setCluster(Attribute cluster) {
        setSpecialAttribute(cluster, CLUSTER_NAME);
    }

    @Override
    public Attribute getOutlier() {
        return getSpecial(OUTLIER_NAME);
    }

    @Override
    public void setOutlier(Attribute outlier) {
        setSpecialAttribute(outlier, OUTLIER_NAME);
    }

    @Override
    public Attribute getCost() {
        return getSpecial(CLASSIFICATION_COST);
    }

    @Override
    public void setCost(Attribute cost) {
        setSpecialAttribute(cost, CLASSIFICATION_COST);
    }

    @Override
    public void setSpecialAttribute(Attribute attribute, String specialName) {
        AttributeRole oldRole = findRoleBySpecialName(specialName);
        if (oldRole != null) {
            remove(oldRole);
        }
        if (attribute != null) {
            remove(attribute);
            AttributeRole role = new AttributeRole(attribute);
            role.setSpecial(specialName);
            ;
            add(role);
        }
    }

    /** Returns a string representation of this attribute set. */
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer(getClass().getSimpleName() + ": ");
        Iterator<AttributeRole> r = allAttributeRoles();
        boolean first = true;
        while (r.hasNext()) {
            if (!first) {
                result.append(", ");
            }
            result.append(r.next());
            first = false;
        }
        return result.toString();
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
