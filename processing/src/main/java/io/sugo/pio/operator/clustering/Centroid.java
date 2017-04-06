package io.sugo.pio.operator.clustering;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.sugo.pio.tools.Tools;

import java.io.Serializable;
import java.util.Collection;


/**
 * This class represents a single centroid used for centroid based clustering. It also provides
 * methods for centroid calculation of a number of examples.
 */
public class Centroid implements Serializable {

    private static final long serialVersionUID = 1L;

    private double[] centroid;

    private double[] centroidSum;
    private int numberOfAssigned = 0;

    public Centroid(int numberOfDimensions) {
        centroid = new double[numberOfDimensions];
        centroidSum = new double[numberOfDimensions];
    }

    @JsonValue
    public double[] getCentroid() {
        return centroid;
    }

    public void setCentroid(double[] coordinates) {
        this.centroid = coordinates;
    }

    public void assignExample(double[] exampleValues) {
        numberOfAssigned++;
        for (int i = 0; i < exampleValues.length; i++) {
            centroidSum[i] += exampleValues[i];
        }
    }

    public boolean finishAssign() {
        double[] newCentroid = new double[centroid.length];
        boolean stable = true;
        for (int i = 0; i < centroid.length; i++) {
            newCentroid[i] = centroidSum[i] / numberOfAssigned;
            stable &= Double.compare(newCentroid[i], centroid[i]) == 0;
        }
        centroid = newCentroid;
        centroidSum = new double[centroidSum.length];
        numberOfAssigned = 0;
        return stable;
    }

    /**
     * This method only returns the first 100 attributes
     */
    public String toString(Collection<String> dimensionNames) {
        StringBuffer buffer = new StringBuffer();
        int i = 0;
        for (String dimName : dimensionNames) {
            buffer.append(dimName + ":\t");
            buffer.append(Tools.formatNumber(centroid[i]) + Tools.getLineSeparator());
            i++;
            if (i > 100) {
                break;
            }
        }
        return buffer.toString();
    }

}
