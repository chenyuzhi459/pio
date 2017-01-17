package io.sugo.pio.operator;

import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeInt;
import io.sugo.pio.ports.InputPorts;
import io.sugo.pio.tools.ParameterService;

import java.util.List;

/**
 */
public final class ProcessRootOperator extends OperatorChain {
    public static final String TYPE = "root_operator";

    /**
     * The property name for &quot;The default random seed (-1: random random seed).&quot;
     */
    public static final String PROPERTY_RAPIDMINER_GENERAL_RANDOMSEED = "rapidminer.general.randomseed";

    public static final String PARAMETER_RANDOM_SEED = "random_seed";

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getFullName() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public OperatorGroup getGroup() {
        return null;
    }

    /**
     * Convenience backport method to get the results of a process.
     */
    public IOContainer getResults(boolean omitNullResults) {
        InputPorts inputPorts = getExecutionUnit(0).getInnerSinks();
        return inputPorts.createIOContainer(false, omitNullResults);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        int seed = 2001;
        String seedProperty = ParameterService.getParameterValue(PROPERTY_RAPIDMINER_GENERAL_RANDOMSEED);
        try {
            if (seedProperty != null) {
                seed = Integer.parseInt(seedProperty);
            }
        } catch (NumberFormatException e) {
        }
        types.add(new ParameterTypeInt(PARAMETER_RANDOM_SEED,
                "Global random seed for random generators (-1 for initialization by system time).", Integer.MIN_VALUE,
                Integer.MAX_VALUE, seed));
        return types;
    }
}
