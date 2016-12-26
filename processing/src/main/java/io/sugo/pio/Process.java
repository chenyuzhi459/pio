package io.sugo.pio;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.ProcessRootOperator;
import io.sugo.pio.operator.IOContainer;
import io.sugo.pio.util.OperatorService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 */
public class Process {

    private final String id = UUID.randomUUID().toString();

    /**
     * This map holds the names of all operators in the process. Operators are automatically
     * registered during adding and unregistered after removal.
     */
    private Map<String, Operator> operatorNameMap = new HashMap<>();

    private ProcessRootOperator rootOperator;

    /**
     * Indicates whether the {@link IOContainer} returned by {@link #run()} might contain
     * <code>null</code> values for empty results.
     */
    private boolean omitNullResults = true;

    @JsonCreator
    public Process(
        @JsonProperty("rootOperator") ProcessRootOperator rootOperator
    ) {
        try {
            setRootOperator(rootOperator);
        } catch (Exception e) {
            throw new RuntimeException("Cannot initialize root operator of the process: " + e.getMessage(), e);
        }
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    public void setRootOperator(ProcessRootOperator rootOperator) {
        this.rootOperator = rootOperator;
        this.operatorNameMap.clear();
        this.rootOperator.setProcess(this);
        this.rootOperator.rename("Root");
    }

    /** Delivers the current root operator. */
    @JsonProperty("rootOperator")
    public ProcessRootOperator getRootOperator() {
        return rootOperator;
    }

    public boolean isOmitNullResults() {
        return omitNullResults;
    }

    public void setOmitNullResults(boolean omitNullResults) {
        this.omitNullResults = omitNullResults;
    }

    /**
     * Starts the process with no input.
     */
    public final IOContainer run() {
        rootOperator.execute();
        IOContainer result = rootOperator.getResults(isOmitNullResults());
        return result;
    }

    /**
     * Returns a &quot;name (i)&quot; if name is already in use. This new name should then be used
     * as operator name.
     */
    public String registerName(final String name, final Operator operator) {
        if (operatorNameMap.get(name) != null) {
            String baseName = name;
            int index = baseName.indexOf(" (");
            if (index >= 0) {
                baseName = baseName.substring(0, index);
            }
            int i = 2;
            while (operatorNameMap.get(baseName + " (" + i + ")") != null) {
                i++;
            }
            String newName = baseName + " (" + i + ")";
            operatorNameMap.put(newName, operator);
            return newName;
        } else {
            operatorNameMap.put(name, operator);
            return name;
        }
    }

    /** This method is used for unregistering a name from the operator name map. */
    public void unregisterName(final String name) {
        operatorNameMap.remove(name);
    }
}
