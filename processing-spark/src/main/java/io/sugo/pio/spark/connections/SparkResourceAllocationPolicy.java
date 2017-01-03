package io.sugo.pio.spark.connections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 */
public enum SparkResourceAllocationPolicy {
    STATIC_HEURISTIC("Static, Heuristic Configuration", true),
    STATIC_MANUAL("Static, Manual Configuration", true),
    STATIC_DEFAULT("Static, Default Configuration", true),
    DYNAMIC("Dynamic Resource Allocation", true),
    STATIC_HEURISTIC_SINGLE_NODE_PUSHDOWN("Static, Heuristic Configuration based on the largest node", false);

    public static final String STATIC_HEURISTIC_PARM_PERCENTAGE = "static_heuristic_parm_percentage";
    public static final int STATIC_HEURISTIC_PARM_PERCENTAGE_DEFAULT = 70;
    public static final String STATIC_HEURISTIC_SINGLE_NODE_PUSHDOWN_PARM_EXECUTOR_PERCENTAGE = "static_heuristic_single_node_pushdown_parm_executor_percentage";
    public static final String STATIC_HEURISTIC_SINGLE_NODE_PUSHDOWN_PARM_DRIVER_MEMORY = "static_heuristic_single_node_pushdown_parm_driver_memory";
    public static final int STATIC_HEURISTIC_SINGLE_NODE_PUSHDOWN_PARM_PERCENTAGE_DEFAULT = 80;
    public static final int STATIC_HEURISTIC_SINGLE_NODE_PUSHDOWN_PARM_DRIVER_MEMORY_DEFAULT = 2048;
    private String displayName;
    private boolean connectionPolicy;

    private SparkResourceAllocationPolicy(String displayName, boolean connectionPolicy) {
        this.displayName = displayName;
        this.connectionPolicy = connectionPolicy;
    }

    public static String[] getAllDisplayablePolicies() {
        ArrayList values = new ArrayList();
        SparkResourceAllocationPolicy[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            SparkResourceAllocationPolicy format = var1[var3];
            if(format.connectionPolicy) {
                values.add(format.displayName);
            }
        }

        return (String[])values.toArray(new String[values.size()]);
    }

    public String toString() {
        return this.name().toLowerCase();
    }

    public String getId() {
        return this.toString();
    }

    public String getDisplayableName() {
        return this.displayName;
    }

    public boolean isConnectionPolicy() {
        return this.connectionPolicy;
    }

    public static SparkResourceAllocationPolicy getFromDisplayableName(String name) {
        SparkResourceAllocationPolicy[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            SparkResourceAllocationPolicy pol = var1[var3];
            if(pol.getDisplayableName().equals(name)) {
                return pol;
            }
        }

        return null;
    }

    public static SparkResourceAllocationPolicy getFromId(String id) {
        SparkResourceAllocationPolicy[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            SparkResourceAllocationPolicy pol = var1[var3];
            if(pol.getId().equals(id)) {
                return pol;
            }
        }

        return null;
    }

    public static SparkResourceAllocationPolicy getDefaultPolicy() {
        return STATIC_HEURISTIC;
    }

    public static int getDefaultHeuristicPercentage() {
        return 70;
    }

    public Map<String, String> getResourceAllocationPolicyParms(int heuristicAllocationPercentage) {
        HashMap parms = new HashMap();
        switch(this.ordinal()) {
            case 1:
                parms.put("static_heuristic_parm_percentage", String.valueOf(heuristicAllocationPercentage));
            default:
                return parms;
        }
    }

    public boolean overrides(SparkResourceAllocationPolicy sparkResourceAllocationPolicy) {
        switch(this.ordinal()) {
            case 2:
                if(sparkResourceAllocationPolicy == STATIC_HEURISTIC) {
                    return true;
                }
            default:
                return false;
        }
    }
}