package io.sugo.pio.example.set;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.ResultObjectAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public abstract class AbstractExampleSet extends ResultObjectAdapter implements ExampleSet {

    private static final long serialVersionUID = 8596141056047402798L;

    /** Maps the id values on the line index in the example table. */
    private Map<Double, int[]> idMap = new HashMap<Double, int[]>();

    /**
     * Clones the example set by invoking a single argument clone constructor. Please note that a
     * cloned example set has no information about the attribute statistics. That means, that
     * attribute statistics must be (re-)calculated after the clone was created.
     */
    @Override
    public ExampleSet clone() {
        try {
            Class<? extends AbstractExampleSet> clazz = getClass();
            java.lang.reflect.Constructor cloneConstructor = clazz.getConstructor(new Class[] { clazz });
            AbstractExampleSet result = (AbstractExampleSet) cloneConstructor.newInstance(new Object[] { this });
            result.idMap = idMap;
            return result;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot clone ExampleSet: " + e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("'" + getClass().getName() + "' does not implement clone constructor!");
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw new RuntimeException("Cannot clone " + getClass().getName() + ": " + e + ". Target: "
                    + e.getTargetException() + ". Cause: " + e.getCause() + ".");
        } catch (InstantiationException e) {
            throw new RuntimeException("Cannot clone " + getClass().getName() + ": " + e);
        }
    }

}
