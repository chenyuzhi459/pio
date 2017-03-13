/**
 * Copyright (C) 2001-2016 by RapidMiner and the contributors
 * <p>
 * Complete list of developers available at our web site:
 * <p>
 * http://rapidminer.com
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */
package io.sugo.pio.operator.preprocessing.sampling;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.set.MappedExampleSet;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.operator.OperatorVersion;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.operator.annotation.ResourceConsumptionEstimator;
import io.sugo.pio.parameter.*;
import io.sugo.pio.parameter.conditions.EqualTypeCondition;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.ports.metadata.MDInteger;
import io.sugo.pio.tools.OperatorResourceConsumptionHandler;
import io.sugo.pio.tools.RandomGenerator;

import java.util.List;


/**
 * This operator constructs a bootstrapped sample from the given example set. That means that a
 * sampling with replacement will be performed. The usual sample size is the number of original
 * examples. This operator also offers the possibility to create the inverse example set, i.e. an
 * example set containing all examples which are not part of the bootstrapped example set. This
 * inverse example set might be used for a bootstrapped validation (together with an
 * {@link IteratingPerformanceAverage} operator.
 */
public class BootstrappingOperator extends AbstractSamplingOperator {

    public static final String PARAMETER_SAMPLE = "sample";

    public static final String[] SAMPLE_MODES = {"absolute", "relative"};

    public static final int SAMPLE_ABSOLUTE = 0;

    public static final int SAMPLE_RELATIVE = 1;

    /**
     * The parameter name for &quot;The fraction of examples which should be sampled&quot;
     */
    public static final String PARAMETER_SAMPLE_SIZE = "sample_size";

    /**
     * The parameter name for &quot;This ratio determines the size of the new example set.&quot;
     */
    public static final String PARAMETER_SAMPLE_RATIO = "sample_ratio";

    public static final String PARAMETER_USE_WEIGHTS = "use_weights";

    private static final OperatorVersion VERSION_6_4_0 = new OperatorVersion(6, 4, 0);

    public BootstrappingOperator() {
        super();
    }

    @Override
    public String getDefaultFullName() {
        return null;
    }

    @Override
    public OperatorGroup getGroup() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    protected MDInteger getSampledSize(ExampleSetMetaData emd) throws UndefinedParameterError {
        switch (getParameterAsInt(PARAMETER_SAMPLE)) {
            case SAMPLE_ABSOLUTE:
                return new MDInteger(getParameterAsInt(PARAMETER_SAMPLE_SIZE));
            case SAMPLE_RELATIVE:
                MDInteger number = emd.getNumberOfExamples();
                number.multiply(getParameterAsDouble(PARAMETER_SAMPLE_RATIO));
                return number;
            default:
                return new MDInteger();
        }
    }

    @Override
    public ExampleSet apply(ExampleSet exampleSet) throws OperatorException {
        int size = exampleSet.size();

        // cannot bootstrap without any examples
        if (size < 1) {
            throw new UserError(this, "pio.error.operator.exampleset_empty");
        }

        RandomGenerator random = RandomGenerator.getRandomGenerator(this);
        switch (getParameterAsInt(PARAMETER_SAMPLE)) {
            case SAMPLE_ABSOLUTE:
                size = getParameterAsInt(PARAMETER_SAMPLE_SIZE);
                break;
            case SAMPLE_RELATIVE:
                size = (int) Math.round(exampleSet.size() * getParameterAsDouble(PARAMETER_SAMPLE_RATIO));
                break;
        }

        int[] mapping = null;
        if (getParameterAsBoolean(PARAMETER_USE_WEIGHTS) && exampleSet.getAttributes().getWeight() != null) {
            mapping = MappedExampleSet.createWeightedBootstrappingMapping(exampleSet, size, random);
        } else {
            mapping = MappedExampleSet.createBootstrappingMapping(exampleSet, size, random);
        }

        // create and materialize example set
        ExampleSet mappedExampleSet = new MappedExampleSet(exampleSet, mapping, true);
        /*if (getCompatibilityLevel().isAbove(VERSION_6_4_0)) {
            int type = DataRowFactory.TYPE_DOUBLE_ARRAY;
            if (exampleSet.size() > 0) {
                type = exampleSet.getExampleTable().getDataRow(0).getType();
            }
            mappedExampleSet = MaterializeDataInMemory.materializeExampleSet(mappedExampleSet, type);
        }*/
        return mappedExampleSet;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        ParameterType type = new ParameterTypeCategory(PARAMETER_SAMPLE, "Determines how the amount of data is specified.",
                SAMPLE_MODES, SAMPLE_RELATIVE);
        type.setExpert(false);
        types.add(type);
        type = new ParameterTypeInt(PARAMETER_SAMPLE_SIZE, "The number of examples which should be sampled", 1,
                Integer.MAX_VALUE, 100);
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_SAMPLE, SAMPLE_MODES, true, SAMPLE_ABSOLUTE));
        type.setExpert(false);
        types.add(type);
        type = new ParameterTypeDouble(PARAMETER_SAMPLE_RATIO, "This ratio determines the size of the new example set.",
                0.0d, Double.POSITIVE_INFINITY, 1.0d);
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_SAMPLE, SAMPLE_MODES, true, SAMPLE_RELATIVE));
        type.setExpert(false);
        types.add(type);
        type = new ParameterTypeBoolean(PARAMETER_USE_WEIGHTS,
                "If checked, example weights will be considered during the bootstrapping if such weights are present.", true);
        type.setExpert(false);
        types.add(type);
        types.addAll(RandomGenerator.getRandomGeneratorParameters(this));
        return types;
    }

	/*@Override
    public OperatorVersion[] getIncompatibleVersionChanges() {
		return (OperatorVersion[]) ArrayUtils.addAll(super.getIncompatibleVersionChanges(),
				new OperatorVersion[] { VERSION_6_4_0 });
	}*/

    //	@Override
    public ResourceConsumptionEstimator getResourceConsumptionEstimator() {
        return OperatorResourceConsumptionHandler.getResourceConsumptionEstimator(getInputPort(),
                BootstrappingOperator.class, null);
    }
}
