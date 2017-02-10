package io.sugo.pio.cli;

import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;

/**
 */
public class YarnHAUtil {
    public static void setRMHA(Configuration conf) {
        String rmId = getRMHAId(conf);
        conf.set(YarnConfiguration.RM_ADDRESS, conf.get(addSuffix(YarnConfiguration.RM_ADDRESS, rmId)));
        conf.set(YarnConfiguration.RM_SCHEDULER_ADDRESS, conf.get(addSuffix(YarnConfiguration.RM_SCHEDULER_ADDRESS, rmId)));
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
        return  conf.getStringCollection(YarnConfiguration.RM_HA_IDS);
    }

    public static String getRMHAId(Configuration conf) {
        int found = 0;
        String currentRMId = conf.getTrimmed(YarnConfiguration.RM_HA_ID);
        if(currentRMId == null) {
            for(String rmId : getRMHAIds(conf)) {
                String key = addSuffix(YarnConfiguration.RM_ADDRESS, rmId);
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
            port = YarnConfiguration.DEFAULT_RM_PORT;
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
