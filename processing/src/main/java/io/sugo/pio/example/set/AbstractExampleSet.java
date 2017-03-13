package io.sugo.pio.example.set;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.datatable.DataTable;
import io.sugo.pio.datatable.DataTableExampleSetAdapter;
import io.sugo.pio.example.*;
import io.sugo.pio.operator.IOContainer;
import io.sugo.pio.operator.error.MissingIOObjectException;
import io.sugo.pio.operator.ResultObjectAdapter;

import java.util.*;

/**
 */
public abstract class AbstractExampleSet extends ResultObjectAdapter implements ExampleSet {

    private static final long serialVersionUID = 8596141056047402798L;

    /** Maps attribute names to list of statistics objects. */
//    @JsonProperty
    private final Map<String, List<Statistics>> statisticsMap = new HashMap<String, List<Statistics>>();

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

    /**
     * This method is used to create a {@link DataTable} from this example set. The default
     * implementation returns an instance of {@link DataTableExampleSetAdapter}. The given
     * IOContainer is used to check if there are compatible attribute weights which would used as
     * column weights of the returned table. Subclasses might want to override this method in order
     * to allow for other data tables.
     */
    public DataTable createDataTable(IOContainer container) {
        AttributeWeights weights = null;
        if (container != null) {
            try {
                weights = container.get(AttributeWeights.class);
                for (Attribute attribute : getAttributes()) {
                    double weight = weights.getWeight(attribute.getName());
                    if (Double.isNaN(weight)) { // not compatible
                        weights = null;
                        break;
                    }
                }
            } catch (MissingIOObjectException e) {
            }
        }
        return new DataTableExampleSetAdapter(this, weights);
    }

    /**
     * Recalculates the attribute statistics for all attributes. They are average value, variance,
     * minimum, and maximum. For nominal attributes the occurences for all values are counted. This
     * method collects all attributes (regular and special) in a list and invokes
     * <code>recalculateAttributeStatistics(List attributes)</code> and performs only one data scan.
     * <p>
     * The statistics calculation is stopped by {@link Thread#interrupt()}.
     */
    @Override
    public void recalculateAllAttributeStatistics() {
        List<Attribute> allAttributes = new ArrayList<Attribute>();
        Iterator<Attribute> a = getAttributes().allAttributes();
        while (a.hasNext()) {
            allAttributes.add(a.next());
        }
        recalculateAttributeStatistics(allAttributes);
    }

    /**
     * Recalculate the attribute statistics of the given attribute.
     * <p>
     * The statistics calculation is stopped by {@link Thread#interrupt()}.
     */
    @Override
    public void recalculateAttributeStatistics(Attribute attribute) {
        List<Attribute> allAttributes = new ArrayList<Attribute>();
        allAttributes.add(attribute);
        recalculateAttributeStatistics(allAttributes);
    }

    /**
     * Here the Example Set is parsed only once, all the information is retained for each example
     * set.
     * <p>
     * The statistics calculation is stopped by {@link Thread#interrupt()}.
     */
    private void recalculateAttributeStatistics(List<Attribute> attributeList) {
        // do nothing if not desired
        if (attributeList.size() == 0) {
            return;
        } else {
            // init statistics
//            resetAttributeStatistics(attributeList);
            // init statistics
            for (Attribute attribute : attributeList) {
                Iterator<Statistics> stats = attribute.getAllStatistics();
                while (stats.hasNext()) {
                    Statistics statistics = stats.next();
                    statistics.startCounting(attribute);
                }
            }

            // calculate statistics
            Attribute weightAttribute = getAttributes().getWeight();
            if ((weightAttribute != null) && (!weightAttribute.isNumerical())) {
                weightAttribute = null;
            }
            for (Example example : this) {
                for (Attribute attribute : attributeList) {
                    double value = example.getValue(attribute);
                    double weight = 1.0d;
                    if (weightAttribute != null) {
                        weight = example.getValue(weightAttribute);
                    }
                    Iterator<Statistics> stats = attribute.getAllStatistics();
                    while (stats.hasNext()) {
                        Statistics statistics = stats.next();
                        statistics.count(value, weight);
                    }
                }
                /*if (Thread.currentThread().isInterrupted()) {
                    // statistics is only partly calculated, reset
                    resetAttributeStatistics(attributeList);
                    return;
                }*/
            }

            // store cloned statistics
            for (Attribute attribute : attributeList) {
                List<Statistics> statisticsList = statisticsMap.get(attribute.getName());
                // no stats known for this attribute at all --> new list
                if (statisticsList == null) {
                    statisticsList = new LinkedList<Statistics>();
                    statisticsMap.put(attribute.getName(), statisticsList);
                }

                // in all cases: clear the list before adding new stats (clone of the calculations)
                statisticsList.clear();

                Iterator<Statistics> stats = attribute.getAllStatistics();
                while (stats.hasNext()) {
                    Statistics statistics = (Statistics) stats.next().clone();
                    statisticsList.add(statistics);
                }
                /*if (Thread.currentThread().isInterrupted()) {
                    return;
                }*/
            }
        }
    }


    /**
     * Resets the statistics for all attributes from attributeList.
     *
     * @param attributeList
     *            the attributes for which to reset the statistics
     */
    private void resetAttributeStatistics(List<Attribute> attributeList) {
        for (Attribute attribute : attributeList) {
            for (Iterator<Statistics> stats = attribute.getAllStatistics(); stats.hasNext();) {
                Statistics statistics = stats.next();
                statistics.startCounting(attribute);
            }
        }
    }

    /**
     * Returns the desired statistic for the given attribute. This method should be preferred over
     * the deprecated method Attribute#getStatistics(String) since it correctly calculates and keep
     * the statistics for the current example set and does not overwrite the statistics in the
     * attribute. Invokes the method {@link #getStatistics(Attribute, String, String)} with a null
     * statistics parameter.
     */
    @Override
    public double getStatistics(Attribute attribute, String statisticsName) {
        return getStatistics(attribute, statisticsName, null);
    }

    /**
     * Returns the desired statistic for the given attribute. This method should be preferred over
     * the deprecated method Attribute#getStatistics(String) since it correctly calculates and keep
     * the statistics for the current example set and does not overwrite the statistics in the
     * attribute. If the statistics were not calculated before (via one of the recalculate methods)
     * this method will return NaN. If no statistics is available for the given name, also NaN is
     * returned.
     */
    @Override
    public double getStatistics(Attribute attribute, String statisticsName, String statisticsParameter) {
        List<Statistics> statisticsList = statisticsMap.get(attribute.getName());
        if (statisticsList == null) {
            return Double.NaN;
        }

        for (Statistics statistics : statisticsList) {
            if (statistics.handleStatistics(statisticsName)) {
                return statistics.getStatistics(attribute, statisticsName, statisticsParameter);
            }
        }

        return Double.NaN;
    }


}
