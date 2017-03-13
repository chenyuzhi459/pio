package io.sugo.pio.operator.learner.associations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import io.sugo.pio.tools.Tools;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;


/**
 * <p>
 * An association rule which can be created from a frequent item set.
 * </p>
 * <p>
 * <p>
 * Note: this class has a natural ordering that is inconsistent with equals.
 * </p>
 */
public class AssociationRule implements Serializable, Comparable<AssociationRule> {

    private static final long serialVersionUID = -4788528227281876533L;

    @JsonProperty
    private double confidence, totalSupport, lift, laplace, gain, ps, conviction;

    private Collection<Item> premise;

    private Collection<Item> conclusion;

    public AssociationRule(Collection<Item> premise, Collection<Item> conclusion, double totalSupport) {
        this.premise = premise;
        this.conclusion = conclusion;
        this.totalSupport = totalSupport;
    }

    public double getGain() {
        return gain;
    }

    public void setGain(double gain) {
        this.gain = gain;
    }

    public double getConviction() {
        return conviction;
    }

    public void setConviction(double conviction) {
        this.conviction = conviction;
    }

    public double getLaplace() {
        return laplace;
    }

    public void setLaplace(double laplace) {
        this.laplace = laplace;
    }

    public double getLift() {
        return lift;
    }

    public void setLift(double lift) {
        this.lift = lift;
    }

    public double getPs() {
        return ps;
    }

    public void setPs(double ps) {
        this.ps = ps;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public double getConfidence() {
        return this.confidence;
    }

    public double getTotalSupport() {
        return this.totalSupport;
    }

    @JsonProperty
    public String getPremises() {
        StringBuilder sb = new StringBuilder();
        Iterator<Item> itemIterator = premise.iterator();
        while (itemIterator.hasNext()) {
            Item item = itemIterator.next();
            sb.append(item.getName()).append(",");
        }

        String result = sb.toString();
        if (!Strings.isNullOrEmpty(result)) {
            result = result.substring(0, result.length()-1);
        }

        return result;
    }

    @JsonProperty
    public String getConclusion() {
        StringBuilder sb = new StringBuilder();
        Iterator<Item> itemIterator = conclusion.iterator();
        while (itemIterator.hasNext()) {
            Item item = itemIterator.next();
            sb.append(item.getName()).append(",");
        }

        String result = sb.toString();
        if (!Strings.isNullOrEmpty(result)) {
            result = result.substring(0, result.length()-1);
        }

        return result;
    }

    public Iterator<Item> getPremiseItems() {
        return premise.iterator();
    }

    public Iterator<Item> getConclusionItems() {
        return conclusion.iterator();
    }

    public String toPremiseString() {
        return premise.toString();
    }

    public String toConclusionString() {
        return conclusion.toString();
    }

    @Override
    public int compareTo(AssociationRule o) {
        return Double.compare(this.confidence, o.confidence);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AssociationRule)) {
            return false;
        }
        AssociationRule other = (AssociationRule) o;
        return premise.toString().equals(other.premise.toString())
                && conclusion.toString().equals(other.conclusion.toString()) && this.confidence == other.confidence;
    }

    @Override
    public int hashCode() {
        return premise.toString().hashCode() ^ conclusion.toString().hashCode() ^ Double.valueOf(this.confidence).hashCode();
    }

    @Override
    @JsonProperty("description")
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(premise.toString());
        buffer.append(" --> ");
        buffer.append(conclusion.toString());
        buffer.append(" (confidence: ");
        buffer.append(Tools.formatNumber(confidence));
        buffer.append(")");
        return buffer.toString();
    }
}