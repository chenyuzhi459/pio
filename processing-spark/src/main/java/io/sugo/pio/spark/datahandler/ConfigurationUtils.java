package io.sugo.pio.spark.datahandler;

import io.sugo.pio.spark.connections.ConfigurationMapBuilder;
import io.sugo.pio.spark.connections.ConfigurationMapBuilder.ConfigurationMap;
import org.apache.hadoop.conf.Configuration;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 */
public class ConfigurationUtils {
    public ConfigurationUtils() {
    }

    public static String[] getNameNodeAddresses(Configuration conf) {
        ConfigurationMap tempConfMap = new ConfigurationMap();
        for(Map.Entry<String,String> entry: conf) {
            tempConfMap.put(entry.getKey(), entry.getValue());
        }
        return getNameNodeAddresses(tempConfMap);
    }

    public static String[] getNameNodeAddresses(ConfigurationMap conf) {
        LinkedHashSet hosts = new LinkedHashSet();
        String nameServices = conf.getOrDefault("dfs.nameservices", "");
        LinkedHashSet<String> pairs = new LinkedHashSet();
        String[] nameNodeAddress = nameServices.split(",");
        int pair = nameNodeAddress.length;

        for(int host = 0; host < pair; ++host) {
            String nameService = nameNodeAddress[host];
            String nameNodes = conf.getOrDefault("dfs.ha.namenodes." + nameService, "");
            if(!nameNodes.isEmpty()) {
                String[] var9 = nameNodes.split(",");
                int var10 = var9.length;

                for(int var11 = 0; var11 < var10; ++var11) {
                    String nn = var9[var11];
                    pairs.add(nameService + "." + nn);
                }
            }
        }

        Iterator<String> pairIterator = pairs.iterator();

        while(pairIterator.hasNext()) {
            String var15 = pairIterator.next();
            String var16 = conf.getOrDefault("dfs.namenode.rpc-address." + var15, "");
            if(!var16.isEmpty()) {
                hosts.add(var16);
            }
        }

        if(hosts.isEmpty()) {
            String var14 = conf.get("fs.defaultFS");
            if(var14 != null) {
                hosts.add(var14.substring(var14.indexOf("://") + "://".length(), var14.endsWith("/")?var14.length() - 1:var14.length()));
            }
        }

        return (String[])hosts.toArray(new String[0]);
    }

    public static String[] getResourceManagerAddresses(Configuration conf) {
        ConfigurationMap tempConfMap = new ConfigurationMap();
        for(Map.Entry<String,String> entry: conf) {
            tempConfMap.put(entry.getKey(), entry.getValue());
        }
        return getResourceManagerAddresses(tempConfMap);
    }

    public static String[] getResourceManagerAddresses(ConfigurationMap conf) {
        LinkedHashSet hosts = new LinkedHashSet();
        if(Boolean.parseBoolean(conf.get("yarn.resourcemanager.ha.enabled"))) {
            String[] rmids = conf.getOrDefault("yarn.resourcemanager.ha.rm-ids", "").split(",");
            int length = rmids.length;

            for(int i = 0; i < length; ++i) {
                String rmId = rmids[i];
                String defaultHost = conf.get("yarn.resourcemanager.hostname." + rmId);
                String host;
                if((host = conf.get("yarn.resourcemanager.address." + rmId)) != null) {
                    hosts.add(host);
                } else {
                    if(defaultHost == null) {
                        return null;
                    }

                    hosts.add(defaultHost + ":" + 8032);
                }

                if((host = conf.get("yarn.resourcemanager.scheduler.address." + rmId)) != null) {
                    hosts.add(host);
                } else {
                    if(defaultHost == null) {
                        return null;
                    }

                    hosts.add(defaultHost + ":" + 8030);
                }

                if((host = conf.get("yarn.resourcemanager.resource-tracker.address." + rmId)) != null) {
                    hosts.add(host);
                } else {
                    if(defaultHost == null) {
                        return null;
                    }

                    hosts.add(defaultHost + ":" + 8031);
                }
            }
        }

        if(hosts.isEmpty()) {
            hosts.add(conf.get("yarn.resourcemanager.address"));
            hosts.add(conf.get("yarn.resourcemanager.scheduler.address"));
            hosts.add(conf.get("yarn.resourcemanager.resource-tracker.address"));
            if(hosts.contains(null)) {
                return null;
            }
        }

        return (String[])hosts.toArray(new String[0]);
    }
}
