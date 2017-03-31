package io.sugo.pio.operator.learner.tree;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.Example;

import java.util.HashSet;
import java.util.Set;

/**
 * SplitCondition for Radoop's Decision Tree. Returns true if the value of the desired attribute is
 * in the given set.
 */
public class ContainsSplitCondition extends AbstractSplitCondition {

    private static final long serialVersionUID = 8093614015273139537L;

    private int maxDisplayedCategories = 2;
    private Set<String> categories;

    public ContainsSplitCondition(String attributeName, String[] splittingCategories) {
        super(attributeName);
        categories = new HashSet<String>();
        for (String cat : splittingCategories) {
            categories.add(cat);
        }
    }

    @Override
    public String getRelation() {
        return "in";
    }

    @Override
    public String getValueString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        int i = 0;
        for (String cat : categories) {
            if (i != 0) {
                sb.append(", ");
            }

            if (i == maxDisplayedCategories) {
                sb.append("...");
                break;
            }
            sb.append(cat);
            i++;
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean test(Example example) {
        return categories.contains(example.getValueAsString(example.getAttributes().get(getAttributeName())));
    }

    @Override
    public String toString() {
        return getAttributeName() + " " + getRelation() + " " + getFullValueString();
    }

    private String getFullValueString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (String cat : categories) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(cat);
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    public void setMaxDisplayedCategories(int maxDisplayedCategories) {
        this.maxDisplayedCategories = maxDisplayedCategories;
    }

    @JsonProperty
    public Set<String> getCategories() {
        return categories;
    }
}
