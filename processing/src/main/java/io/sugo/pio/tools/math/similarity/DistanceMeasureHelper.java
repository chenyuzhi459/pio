package io.sugo.pio.tools.math.similarity;

import com.metamx.common.logger.Logger;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.tools.Observable;
import io.sugo.pio.tools.Observer;


/**
 * This helper class installs and uninstalls additional ports at operators that operate on distance
 * measures. It registers itself as an observer of the operator's parameters and instantiates a new
 * {@link DistanceMeasure} whenever one of the parameters
 * {@link DistanceMeasures#PARAMETER_MEASURE_TYPES},
 * {@link DistanceMeasures#PARAMETER_MIXED_MEASURE},
 * {@link DistanceMeasures#PARAMETER_NUMERICAL_MEASURE},
 * {@link DistanceMeasures#PARAMETER_NOMINAL_MEASURE}, or
 * {@link DistanceMeasures#PARAMETER_DIVERGENCE} changes. If the chosen {@link DistanceMeasure}
 * overrides
 * {@link DistanceMeasure#installAdditionalPorts(io.sugo.pio.ports.InputPorts, io.sugo.pio.parameter.ParameterHandler)}
 * this may install new ports at the operator.
 *
 * In its {@link Operator#doWork()} method, the operator may call
 * {@link #getInitializedMeasure(ExampleSet)} in order to initialize the distance measure and obtain
 * it. Call this method only once.
 *
 */
public class DistanceMeasureHelper {

    private static final Logger logger = new Logger(DistanceMeasureHelper.class);

    private Operator operator;
    private DistanceMeasure measure;

    public DistanceMeasureHelper(Operator operator) {
        this.operator = operator;
        operator.getParameters().addObserver(new Observer<String>() {

            @Override
            public void update(Observable<String> observable, String arg) {
                if (DistanceMeasures.PARAMETER_MEASURE_TYPES.equals(arg)
                        || DistanceMeasures.PARAMETER_MIXED_MEASURE.equals(arg)
                        || DistanceMeasures.PARAMETER_NUMERICAL_MEASURE.equals(arg)
                        || DistanceMeasures.PARAMETER_NOMINAL_MEASURE.equals(arg)
                        || DistanceMeasures.PARAMETER_DIVERGENCE.equals(arg)) {
                    updateMeasure();
                }
            }
        }, false);
        updateMeasure();
    }

    private void updateMeasure() {
        if (measure != null) {
            measure.uninstallAdditionalPorts(operator.getInputPorts());
        }
        try {
            measure = DistanceMeasures.createMeasure(operator);
        } catch (UndefinedParameterError e) {
            logger.warn("While updating distance measure: " + e.toString());
        } catch (OperatorException e) {
            logger.warn("While updating distance measure: " + e.toString());
        }
        if (measure != null) {
            measure.installAdditionalPorts(operator.getInputPorts(), operator);
        }
    }

    public DistanceMeasure getInitializedMeasure(ExampleSet exampleSet) throws OperatorException {
        updateMeasure();
        measure.init(exampleSet, operator);
        return measure;
    }

    public int getSelectedMeasureType() throws UndefinedParameterError {
        return DistanceMeasures.getSelectedMeasureType(operator);
    }
}
