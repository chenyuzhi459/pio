package io.sugo.pio.cli;

import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;

/**
 */
public class YarnHAUtil {
    ////////////////////////////////
    // Resource Manager Configs
    ////////////////////////////////
    public static final String RM_PREFIX = "yarn.resourcemanager.";

    /** The address of the applications manager interface in the RM.*/
    public static final String RM_ADDRESS =
            RM_PREFIX + "address";
    public static final int DEFAULT_RM_PORT = 8032;

    /** The address of the scheduler interface.*/
    public static final String RM_SCHEDULER_ADDRESS =
            RM_PREFIX + "scheduler.address";

    /** HA related configs */
    public static final String RM_HA_PREFIX = RM_PREFIX + "ha.";
    public static final String RM_HA_IDS = RM_HA_PREFIX + "rm-ids";
    public static final String RM_HA_ID = RM_HA_PREFIX + "id";

    public static void setRMHA(Configuration conf) {
        String rmId = getRMHAId(conf);
        conf.set(RM_ADDRESS, conf.get(addSuffix(RM_ADDRESS, rmId)));
        conf.set(RM_SCHEDULER_ADDRESS, conf.get(addSuffix(RM_SCHEDULER_ADDRESS, rmId)));
    }

    /** Add non empty and non null suffix to a key */
    public static String addSuffix(String key, String suffix) {
        if (suffix == null || suffix.isEmpty()) {
            return key;
        }
        if (suffix.startsWith(".")) {
            throw new IllegalArgumentException("suffix '" + suffix + "' should not " +
                    "already have '.' prepended.");
        }
        return key + "." + suffix;
    }

    /**
     * @param conf Configuration. Please use getRMHAIds to check.
     * @return RM Ids on success
     */
    public static Collection<String> getRMHAIds(Configuration conf) {
        return  conf.getStringCollection(RM_HA_IDS);
    }

    public static String getRMHAId(Configuration conf) {
        int found = 0;
        String currentRMId = conf.getTrimmed(RM_HA_ID);
        if(currentRMId == null) {
            for(String rmId : getRMHAIds(conf)) {
                String key = addSuffix(RM_ADDRESS, rmId);
                String addr = conf.get(key);
                if (addr == null) {
                    continue;
                }
                if(valid(addr)) {
                    currentRMId = rmId.trim();
                    found++;
                }
            }
        }
        if (found > 1) { // Only one address must match the local address
            String msg = "The HA Configuration has multiple addresses that match "
                    + "local node's address.";
            throw new HadoopIllegalArgumentException(msg);
        }
        return currentRMId;
    }

    private static boolean valid(String address) {
        String[] hostAndPort = address.split(":");
        String host;
        int port;
        if (hostAndPort.length == 1) {
            host = hostAndPort[0];
            port = DEFAULT_RM_PORT;
        } else {
            host = hostAndPort[0];
            port = Integer.parseInt(hostAndPort[1]);
        }

        try (Socket ignored = new Socket(host, port)) {
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }
}
