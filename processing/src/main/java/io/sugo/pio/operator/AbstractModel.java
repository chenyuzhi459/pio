package io.sugo.pio.operator;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.set.HeaderExampleSet;
import io.sugo.pio.operator.error.UnsupportedApplicationParameterError;

/**
 */
public abstract class AbstractModel extends ResultObjectAdapter implements Model {

    private static final long serialVersionUID = 1640348739650532634L;

    /**
     * This header example set contains all important nominal mappings of all training attributes.
     * These mappings are necessary in order to ensure that the internally used double values
     * encoding nominal values are equal for the training and application data sets.
     */
    private HeaderExampleSet headerExampleSet;

    /**
     * This Operator will be used to check whether the currently running Process was stopped. If it
     * is <code>null</code> nothing will happen else checkForStop will be called.
     */
    private Operator operator = null;

    /**
     * This flag signalizes the apply method if progress in the {@link OperatorProgress} from {@link #getOperator()} should be shown.
     */
    private boolean showProgress = false;

    /**
     * Created a new model which was built on the given example set. Please note that the given
     * example set is automatically transformed into a {@link HeaderExampleSet} which means that no
     * reference to the data itself is kept but only to the header, i.e. to the attribute meta
     * descriptions.
     */
    protected AbstractModel(ExampleSet exampleSet) {
        if (exampleSet != null) {
            this.headerExampleSet = new HeaderExampleSet(exampleSet);
        }
    }

    /**
     * Delivers the training header example set, i.e. the header of the example set (without data
     * reference) which was used for creating this model. Might return null.
     */
    @Override
    public HeaderExampleSet getTrainingHeader() {
        return this.headerExampleSet;
    }

    /**
     * Throws a UserError since most models should not allow additional parameters during
     * application. However, subclasses may overwrite this method.
     */
    @Override
    public void setParameter(String key, Object value) throws OperatorException {
        throw new UnsupportedApplicationParameterError(null, getName(), key);
    }


    /**
     * delivers the set Operator or null if no Operator was set.
     */
    public Operator getOperator() {
        return operator;
    }

    /**
     * If the Operator was set the Model will check for stop by calling checkForStop() at the
     * Operator. If the Operator is set to null nothing will happen and the Model will no longer
     * checkForStop.
     *
     * @param operator If {@code true} this operator will check for stop by calling checkForStop()
     */
    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    /**
     * If this flag is set to {@code true}, the apply method can show the progress in the {@link OperatorProgress} from {@link #getOperator()}
     *
     * @param showProgress When {@code true} progress will be shown when applying.
     */
    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    /**
     * {@code true} if progress should be shown while applying this model.
     */
    public boolean getShowProgress() {
        return showProgress;
    }
}
