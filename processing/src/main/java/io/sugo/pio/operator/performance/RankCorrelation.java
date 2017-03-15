package io.sugo.pio.operator.performance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.math.Averagable;


/**
 * Computes either the Spearman (rho) or Kendall (tau-b) rank correlation between the actual label
 * and predicted values of an example set. Since ranking is involved, neither correlation is
 * averageable.
 */
public class RankCorrelation extends MeasuredPerformance {

    private static final long serialVersionUID = 6908369375703683363L;

    public static final String[] NAMES = {"spearman_rho", "kendall_tau"};

    public static final String[] DESCRIPTIONS = {
            I18N.getMessage("pio.RankCorrelation.description1"),
            I18N.getMessage("pio.RankCorrelation.description2")
    };

    public static final int RHO = 0;
    public static final int TAU = 1;

    @JsonProperty
    private double counter = 0; // example count

    @JsonProperty
    private double value = Double.NaN;

    @JsonProperty
    private int type = RHO;

    /**
     * Default constructor
     */
    public RankCorrelation() {
        this(RHO);
    }

    /**
     * Constructor with user-specified choice of correlation coefficient. User specifies RHO or TAU.
     *
     * @param type coefficient type with coefficient choice
     */
    public RankCorrelation(int type) {
        this.type = type;
    }

    public RankCorrelation(RankCorrelation rc) {
        super(rc);
        this.type = rc.type;
        this.value = rc.value;
        this.counter = rc.counter;
    }

    /**
     * Does nothing. Everything is done in {@link #startCounting(ExampleSet, boolean)}.
     */
    @Override
    public void countExample(Example example) {
    }

    @Override
    public String getDescription() {
        return DESCRIPTIONS[type];
    }

    @Override
    public double getExampleCount() {
        return counter;
    }

    @Override
    public double getFitness() {
        return getMikroAverage();
    }

    /**
     * Averaging across instances of RankCorrelation is unsupported (?) For now just build the usual
     * average by summing up the values...
     */
    @Override
    protected void buildSingleAverage(Averagable averagable) {
        RankCorrelation other = (RankCorrelation) averagable;
        this.counter += other.counter;
        this.value += other.value;
    }

    @Override
    public double getMikroAverage() {
        return value;
    }

    @Override
    public double getMikroVariance() {
        return Double.NaN;
    }

    @Override
    @JsonProperty
    public String getName() {
        return NAMES[type];
    }

    /**
     * Computes whichever of rho and tau was requested.
     */
    @Override
    public void startCounting(ExampleSet eSet, boolean useExampleWeights) throws OperatorException {
        super.startCounting(eSet, useExampleWeights);
        this.counter = eSet.size();
        if (type == RHO) {
            this.value = RankStatistics.rho(eSet, eSet.getAttributes().getLabel(), eSet.getAttributes().getPredictedLabel());
        } else {
            this.value = RankStatistics.tau_b(eSet, eSet.getAttributes().getLabel(), eSet.getAttributes()
                    .getPredictedLabel());
        }

    }
}
