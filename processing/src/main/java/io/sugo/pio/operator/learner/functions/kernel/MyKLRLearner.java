package io.sugo.pio.operator.learner.functions.kernel;

import com.metamx.common.logger.Logger;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.OperatorCapability;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.examples.SVMExamples;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.kernel.Kernel;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.svm.SVMInterface;
import io.sugo.pio.operator.learner.functions.kernel.logistic.KLR;
import io.sugo.pio.parameter.ParameterType;

import java.util.Iterator;
import java.util.List;


/**
 * This is the Java implementation of <em>myKLR</em> by Stefan R&uuml;ping. myKLR is a tool for
 * large scale kernel logistic regression based on the algorithm of Keerthi/etal/2003 and the code
 * of mySVM.
 */
public class MyKLRLearner extends AbstractMySVMLearner {

    private static final Logger logger = new Logger(MyKLRLearner.class);

    public MyKLRLearner() {
        super();
    }

    @Override
    public String getDefaultFullName() {
        return I18N.getMessage("pio.MyKLRLearner.name");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.classification;
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.MyKLRLearner.description");
    }

    @Override
    public int getSequence() {
        return 1;
    }

    @Override
    public boolean supportsCapability(OperatorCapability lc) {
        if (lc == OperatorCapability.NUMERICAL_ATTRIBUTES) {
            return true;
        }
        if (lc == OperatorCapability.BINOMINAL_LABEL) {
            return true;
        }
        return false;
    }

    @Override
    public AbstractMySVMModel createSVMModel(ExampleSet exampleSet, SVMExamples sVMExamples, Kernel kernel, int kernelType) {
        logger.info("MyKLRLearner begin to create SVM model...");
        return new MyKLRModel(exampleSet, sVMExamples, kernel, kernelType);
    }

    @Override
    public SVMInterface createSVM(Attribute label, Kernel kernel, SVMExamples sVMExamples,
                                  io.sugo.pio.example.ExampleSet rapidMinerExamples) throws OperatorException {
        logger.info("MyKLRLearner begin to create SVM...");
        if (!label.isNominal()) {
            throw new UserError(this, "pio.error.attribute_must_nominal", new Object[]{"MyKLR", label.getName()});
        }
        return new KLR(this);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();

        // important: myKLR does not support determinition of the value C!
        Iterator<ParameterType> p = types.iterator();
        while (p.hasNext()) {
            ParameterType type = p.next();
            if (type.getKey().equals(PARAMETER_C)) {
                type.setDefaultValue(Double.valueOf(1.0d));
            }
        }

        return types;
    }
}
