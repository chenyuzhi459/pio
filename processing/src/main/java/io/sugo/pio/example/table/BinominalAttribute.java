package io.sugo.pio.example.table;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.tools.Ontology;

/**
 * This class holds all information on a single binary attribute. In addition to the generic
 * attribute fields this class keeps information about the both values and the value to index
 * mappings. If one of the methods designed for numerical attributes was invoked a RuntimeException
 * will be thrown.
 */
public class BinominalAttribute extends NominalAttribute {

    private static final long serialVersionUID = 2932687830235332221L;

    private NominalMapping nominalMapping = new BinominalMapping();

    /**
     * Creates a simple binary attribute which is not part of a series and does not provide a unit
     * string.
     */
    /* pp */BinominalAttribute(String name) {
        super(name, Ontology.BINOMINAL);
    }

    /**
     * Clone constructor.
     */
    private BinominalAttribute(BinominalAttribute a) {
        super(a);
        // this.nominalMapping = (NominalMapping)a.nominalMapping.clone();
        this.nominalMapping = a.nominalMapping;
    }

    /**
     * Clones this attribute.
     */
    @Override
    public Object clone() {
        return new BinominalAttribute(this);
    }

    @Override
    @JsonProperty
    public NominalMapping getMapping() {
        return this.nominalMapping;
    }

    @Override
    public void setMapping(NominalMapping newMapping) {
        this.nominalMapping = newMapping;
    }

    @Override
    public boolean isDateTime() {
        return false;
    }

}
