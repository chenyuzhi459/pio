package io.sugo.pio.example;

import io.sugo.pio.example.table.AttributeTypeException;
import io.sugo.pio.example.table.DataRow;
import io.sugo.pio.example.table.NumericalAttribute;
import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.Tools;

import java.util.Date;

/**
 */
public class Example {
    /** The data for this example. */
    private DataRow data;

    /** The parent example set holding all attribute information for this data row. */
    private ExampleSet parentExampleSet;

    /**
     * Creates a new Example that uses the data stored in a DataRow. The attributes correspond to
     * the regular and special attributes.
     */
    public Example(DataRow data, ExampleSet parentExampleSet) {
        this.data = data;
        this.parentExampleSet = parentExampleSet;
    }

    /** Returns the data row which backs up the example in the example table. */
    public DataRow getDataRow() {
        return data;
    }

    /** Delivers the attributes. */
    public Attributes getAttributes() {
        return parentExampleSet.getAttributes();
    }


    /**
     * Returns the value of attribute a. In the case of nominal attributes, the delivered double
     * value corresponds to an internal index
     */
    public double getValue(Attribute a) {
        return data.get(a);
    }

    /**
     * Returns the nominal value for the given attribute.
     *
     * @throws AttributeTypeException
     *             if the given attribute has the wrong value type
     */
    public String getNominalValue(Attribute a) {
        if (!a.isNominal()) {
            throw new AttributeTypeException("Extraction of nominal example value for non-nominal attribute '" + a.getName()
                    + "' is not possible.");
        }
        double value = getValue(a);
        if (Double.isNaN(value)) {
            return Attribute.MISSING_NOMINAL_VALUE;
        } else {
            return a.getMapping().mapIndex((int) value);
        }
    }

    /**
     * Returns the numerical value for the given attribute.
     *
     * @throws AttributeTypeException
     *             if the given attribute has the wrong value type
     */
    public double getNumericalValue(Attribute a) {
        if (!a.isNumerical()) {
            throw new AttributeTypeException("Extraction of numerical example value for non-numerical attribute '"
                    + a.getName() + "' is not possible.");
        }
        return getValue(a);
    }

    /**
     * Returns the date value for the given attribute.
     *
     * @throws AttributeTypeException
     *             if the given attribute has the wrong value type
     */
    public Date getDateValue(Attribute a) {
        if (!Ontology.ATTRIBUTE_VALUE_TYPE.isA(a.getValueType(), Ontology.DATE_TIME)) {
            throw new AttributeTypeException("Extraction of date example value for non-date attribute '" + a.getName()
                    + "' is not possible.");
        }
        return new Date((long) getValue(a));
    }

    /**
     * Sets the value of attribute a. The attribute a need not necessarily be part of the example
     * set the example is taken from, although this is no good style.
     */
    public void setValue(Attribute a, double value) {
        data.set(a, value);
    }

    /**
     * Sets the value of attribute a which must be a nominal attribute. The attribute a need not
     * necessarily be part of the example set the example is taken from, although this is no good
     * style. Missing values might be given by passing null as second argument.
     */
    public void setValue(Attribute a, String str) {
        if (!a.isNominal()) {
            throw new AttributeTypeException("setValue(Attribute, String) only supported for nominal values!");
        }
        if (str != null) {
            setValue(a, a.getMapping().mapString(str));
        } else {
            setValue(a, Double.NaN);
        }
    }

    /**
     * Returns true if both nominal values are the same (if both attributes are nominal) or if both
     * real values are the same (if both attributes are real values) or false otherwise.
     */
    public boolean equalValue(Attribute first, Attribute second) {
        if (first.isNominal() && second.isNominal()) {
            return getValueAsString(first).equals(getValueAsString(second));
        } else if ((!first.isNominal()) && (!second.isNominal())) {
            return Tools.isEqual(getValue(first), getValue(second));
        } else {
            return false;
        }
    }

    // ---------------------------------------------------------------------------------

    public double getLabel() {
        return getValue(getAttributes().getLabel());
    }

    public void setLabel(double value) {
        setValue(getAttributes().getLabel(), value);
    }

    public double getPredictedLabel() {
        return getValue(getAttributes().getPredictedLabel());
    }

    public void setPredictedLabel(double value) {
        setValue(getAttributes().getPredictedLabel(), value);
    }

    public double getId() {
        return getValue(getAttributes().getId());
    }

    public void setId(double value) {
        setValue(getAttributes().getId(), value);
    }

    public double getWeight() {
        return getValue(getAttributes().getWeight());
    }

    public void setWeight(double value) {
        setValue(getAttributes().getWeight(), value);
    }

    public double getConfidence(String classValue) {
        return getValue(getAttributes().getConfidence(classValue));
    }

    public void setConfidence(String classValue, double confidence) {
        setValue(getAttributes().getSpecial(Attributes.CONFIDENCE_NAME + "_" + classValue), confidence);
    }

    // --------------------------------------------------------------------------------

    /**
     * <p>
     * Returns the value of this attribute as string representation, i.e. the number as string for
     * numerical attributes and the correctly mapped categorical value for nominal values. The used
     * number of fraction digits is unlimited (see
     * {@link NumericalAttribute#DEFAULT_NUMBER_OF_DIGITS} ). Nominal values containing whitespaces
     * will not be quoted.
     * </p>
     *
     * <p>
     * Please note that this method should not be used in order to get the nominal values, please
     * use {@link #getNominalValue(Attribute)} instead.
     * </p>
     */
    public String getValueAsString(Attribute attribute) {
        return getValueAsString(attribute, NumericalAttribute.UNLIMITED_NUMBER_OF_DIGITS, false);
    }

    /**
     * <p>
     * Returns the value of this attribute as string representation, i.e. the number as string for
     * numerical attributes and the correctly mapped categorical value for nominal values. If the
     * value is numerical the given number of fraction digits is used. If the value is numerical,
     * the given number of fraction digits is used. This value must be either one out of
     * {@link NumericalAttribute#DEFAULT_NUMBER_OF_DIGITS} or
     * {@link NumericalAttribute#UNLIMITED_NUMBER_OF_DIGITS} or a number greater or equal to 0. The
     * boolean flag indicates if nominal values containing whitespaces should be quoted with double
     * quotes.
     * </p>
     *
     * <p>
     * Please note that this method should not be used in order to get the nominal values, please
     * use {@link #getNominalValue(Attribute)} instead.
     * </p>
     */
    public String getValueAsString(Attribute attribute, int fractionDigits, boolean quoteNominal) {
        double value = getValue(attribute);
        return attribute.getAsString(value, fractionDigits, quoteNominal);
    }

}
