package io.sugo.pio.example.table;

import io.sugo.pio.example.NominalStatistics;
import io.sugo.pio.example.UnknownStatistics;
import io.sugo.pio.tools.Tools;

/**
 * This class holds all information on a single nominal attribute. In addition to the generic
 * attribute fields this class keeps information about the nominal values and the value to index
 * mappings. If one of the methods designed for numerical attributes was invoked a RuntimeException
 * will be thrown.
 * <p>
 * It will be guaranteed that all values are mapped to indices without any missing values. This
 * could, however, be changed in future versions thus operators should not rely on this fact.
 *
 */
public abstract class NominalAttribute extends AbstractAttribute {

    private static final long serialVersionUID = -3830980883541763869L;

    protected NominalAttribute(String name, int valueType) {
        super(name, valueType);
        registerStatistics(new NominalStatistics());
        registerStatistics(new UnknownStatistics());
    }

    protected NominalAttribute(NominalAttribute other) {
        super(other);
    }

    @Override
    public boolean isNominal() {
        return true;
    }

    @Override
    public boolean isNumerical() {
        return false;
    }

    /**
     * Returns a string representation and maps the value to a string if type is nominal. The number
     * of digits is ignored.
     */
    @Override
    public String getAsString(double value, int digits, boolean quoteNominal) {
        if (Double.isNaN(value)) {
            return "?";
        } else {
            try {
                String result = getMapping().mapIndex((int) value);
                if (quoteNominal) {
                    result = Tools.escape(result);
                    result = "\"" + result + "\"";
                }
                return result;
            } catch (Throwable e) {
                return "?";
            }
        }
    }
}
