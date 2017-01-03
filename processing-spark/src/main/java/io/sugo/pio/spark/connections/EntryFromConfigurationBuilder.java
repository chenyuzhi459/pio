package io.sugo.pio.spark.connections;


import com.google.common.base.Joiner;
import com.google.common.net.HostAndPort;
import io.sugo.pio.spark.datahandler.ConfigurationUtils;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class EntryFromConfigurationBuilder {
    private static final HostAndPort HIGH_AVAILABILITY_FAKE_HOST_AND_PORT = HostAndPort.fromParts("HIGHAVAILABILITY", 1);
    private static final String MULTIPLE_MASTERS_FAKE_HOST = "__MULTIPLE_MASTERS__";
    List<EntryFromConfigurationBuilder.UsedProperties> missingProperties = new ArrayList();

    public static HadoopConnectionEntry createEntry(List<String> files, String name) throws EntryFromConfigurationBuilder.UnsupportedValueException, IOException {
        ConfigurationMapBuilder mapBuilder = new ConfigurationMapBuilder();
        ConfigurationMapBuilder.ConfigurationMap hadoopProps = mapBuilder.readHadoopProperties(files);
        EntryFromConfigurationBuilder entryBuilder = new EntryFromConfigurationBuilder();
        HadoopConnectionEntry entry = entryBuilder.createEntry(hadoopProps, name);
        return entry;
    }

    public HadoopConnectionEntry createEntry(ConfigurationMapBuilder.ConfigurationMap hadoopMap, String name) throws EntryFromConfigurationBuilder.UnsupportedValueException {
        missingProperties = new ArrayList();
        HadoopConnectionEntry result = new HadoopConnectionEntry();
        result.setName(name);
        result.setSingleMasterConfiguration(false);
        result.setMasterAddress(MULTIPLE_MASTERS_FAKE_HOST);
        String executionFramework = hadoopMap.get("mapreduce.framework.name");
        HostAndPort fsHostAndPort = null;
        HostAndPort mapredHostAndPort = null;
        HostAndPort jobHistoryHostAndPort = null;
        String nameServices = null;

        String[] securityAuthentication;
        try {
            fsHostAndPort = getHostAndPort(hadoopMap, EntryFromConfigurationBuilder.UsedProperties.FS_DEFAULT_NAME, "^(?:hdfs://)?(.+):(\\d+)$");
        } catch (EntryFromConfigurationBuilder.UnsupportedValueException e) {
            nameServices = hadoopMap.get(EntryFromConfigurationBuilder.UsedProperties.DFS_NAMESERVICES.getHadoopKey());
            if (nameServices == null) {
                throw e;
            }

            securityAuthentication = ConfigurationUtils.getNameNodeAddresses(hadoopMap);
            if (securityAuthentication != null && securityAuthentication.length > 0) {
                fsHostAndPort = HostAndPort.fromString(securityAuthentication[0]);
            } else {
                fsHostAndPort = HIGH_AVAILABILITY_FAKE_HOST_AND_PORT;
            }
        }

        if (executionFramework == null) {
            missingProperties.add(EntryFromConfigurationBuilder.UsedProperties.FRAMEWORK_NAME);
        } else {
            if (!executionFramework.equals("yarn")) {
                throw new EntryFromConfigurationBuilder.UnsupportedValueException(EntryFromConfigurationBuilder.UsedProperties.FRAMEWORK_NAME, executionFramework);
            }

            boolean securityAuthorization = "true".equals(hadoopMap.get(EntryFromConfigurationBuilder.UsedProperties.RM_HA_ENABLED.getHadoopKey()));
            if (securityAuthorization) {
                securityAuthentication = ConfigurationUtils.getResourceManagerAddresses(hadoopMap);
                if (securityAuthentication != null && securityAuthentication.length > 0) {
                    mapredHostAndPort = HostAndPort.fromString(securityAuthentication[0]);
                } else {
                    mapredHostAndPort = HIGH_AVAILABILITY_FAKE_HOST_AND_PORT;
                }
            } else {
                mapredHostAndPort = getHostAndPort(hadoopMap, EntryFromConfigurationBuilder.UsedProperties.RM_ADDRESS, "^(.+):(\\d+)$");
            }

            if (mapredHostAndPort == null && securityAuthorization) {
                missingProperties.remove(EntryFromConfigurationBuilder.UsedProperties.RM_ADDRESS);
            }

            jobHistoryHostAndPort = getHostAndPort(hadoopMap, EntryFromConfigurationBuilder.UsedProperties.MR_HISTORY_ADDRESS, "^(.+):(\\d+)$");
        }

        if (fsHostAndPort != null) {
            result.setNamenodeAddress(fsHostAndPort.getHostText());
            result.setHdfsPort(fsHostAndPort.getPort());
        }

        if (mapredHostAndPort != null) {
            result.setJobtrackerAddress(mapredHostAndPort.getHostText());
            result.setMapredPort(mapredHostAndPort.getPort());
        }

        if (jobHistoryHostAndPort != null) {
            result.setJobHistoryServerAddress(jobHistoryHostAndPort.getHostText());
            result.setJobHistoryServerPort(jobHistoryHostAndPort.getPort());
        }
        Iterator<Map.Entry<String, String>> iterator = hadoopMap.entrySet().iterator();

        Map.Entry<String, String> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            result.addAdvancedHadoopSetting(new KeyValueEnableElement(entry.getKey(), entry.getValue(), true));
        }

        return nameServices != null ? result : singleMasterHeuristics(result);
    }

    private HostAndPort getHostAndPort(ConfigurationMapBuilder.ConfigurationMap map, EntryFromConfigurationBuilder.UsedProperties property, String pattern) throws EntryFromConfigurationBuilder.UnsupportedValueException {
        String address = getFromMap(map, property);
        if (null == address) {
            return null;
        } else {
            Matcher addressMatcher = Pattern.compile(pattern).matcher(address);
            if (!addressMatcher.find()) {
                throw new EntryFromConfigurationBuilder.UnsupportedValueException(property, address);
            } else {
                String host = addressMatcher.group(1);
                Integer port = Integer.valueOf(addressMatcher.group(2));
                return HostAndPort.fromParts(host, port.intValue());
            }
        }
    }

    private static HadoopConnectionEntry singleMasterHeuristics(HadoopConnectionEntry entry) {
        boolean replicationIsOne = entry.getAdvancedHadoopSettings().contains(new KeyValueEnableElement("dfs.replication", "1", true));
        String nameNodeAddress = entry.getNameNodeAddressMultipleMasters();
        if (!entry.isSingleMasterConfiguration() && !nameNodeAddress.equals(HIGH_AVAILABILITY_FAKE_HOST_AND_PORT.getHostText())
                && nameNodeAddress.equals(entry.getJobTrackerAddressMultipleMasters())
                && nameNodeAddress.equals(entry.getJobHistoryServerAddressMultipleMasters())) {
            HadoopConnectionEntry result = HadoopConnectionEntry.copyEntry(entry);
            result.setMasterAddress(nameNodeAddress);
            if (replicationIsOne) {
                result.setSingleMasterConfiguration(true);
            }

            return result;
        } else {
            return entry;
        }
    }

    private String getFromMap(ConfigurationMapBuilder.ConfigurationMap map, EntryFromConfigurationBuilder.UsedProperties property) {
        String result = map.get(property.getHadoopKey());
        if (null == result) {
            Iterator<String> iterator = property.getDeprecatedKeys().iterator();

            while (iterator.hasNext()) {
                String key = iterator.next();
                result = map.get(key);
                if (result != null) {
                    break;
                }
            }

            if (result == null) {
                missingProperties.add(property);
                return null;
            }
        }

        return result;
    }

    public enum UsedProperties {
        FRAMEWORK_NAME("mapreduce.framework.name", EntryFromConfigurationBuilder.ConfigFile.MAPRED_SITE, new String[0]),
        FS_DEFAULT_NAME("fs.defaultFS", EntryFromConfigurationBuilder.ConfigFile.CORE_SITE, new String[]{"fs.default.name"}),
        DFS_NAMESERVICES("dfs.nameservices", EntryFromConfigurationBuilder.ConfigFile.HDFS_SITE, new String[0]),
        RM_ADDRESS("yarn.resourcemanager.address", EntryFromConfigurationBuilder.ConfigFile.YARN_SITE, new String[0]),
        RM_HA_ENABLED("yarn.resourcemanager.ha.enabled", EntryFromConfigurationBuilder.ConfigFile.YARN_SITE, new String[0]),
        MR_HISTORY_ADDRESS("mapreduce.jobhistory.address", EntryFromConfigurationBuilder.ConfigFile.MAPRED_SITE, new String[0]),
        HADOOP_SECURITY_AUTHORIZATION("hadoop.security.authorization", EntryFromConfigurationBuilder.ConfigFile.CORE_SITE, new String[0]),
        HADOOP_SECURITY_AUTHENTICATION("hadoop.security.authentication", EntryFromConfigurationBuilder.ConfigFile.CORE_SITE, new String[0]);

        private final String hadoopKey;
        private final EntryFromConfigurationBuilder.ConfigFile configFile;
        private final String[] deprecatedKeys;

        private UsedProperties(String hadoopKey, EntryFromConfigurationBuilder.ConfigFile configFile, String[] deprecatedKeys) {
            this.hadoopKey = hadoopKey;
            this.configFile = configFile;
            this.deprecatedKeys = deprecatedKeys;
        }

        public String getHadoopKey() {
            return hadoopKey;
        }

        public EntryFromConfigurationBuilder.ConfigFile getConfigFile() {
            return configFile;
        }

        public List<String> getDeprecatedKeys() {
            return Arrays.asList(deprecatedKeys);
        }
    }

    public static enum ConfigFile {
        CORE_SITE("core-site.xml"),
        HDFS_SITE("hdfs-site.xml"),
        MAPRED_SITE("mapred-site.xml"),
        YARN_SITE("yarn-site.xml");

        public final String name;

        private ConfigFile(String name) {
            this.name = name;
        }
    }

    public static class UnsupportedValueException extends Exception {
        private static final long serialVersionUID = -4087647440937543301L;
        public final EntryFromConfigurationBuilder.UsedProperties property;
        public final String value;

        private static String createMessage(EntryFromConfigurationBuilder.UsedProperties property, String value) {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Unsupported value <" + value + "> for property <" + property.getHadoopKey() + ">. Please check if the right properties are given in configuration file " + property.getConfigFile().name + ".");
            if (property.getDeprecatedKeys().size() != 0) {
                messageBuilder.append(" (You can also use the deprecated property names: ");
                ArrayList deprecatedNames = new ArrayList();
                Iterator var4 = property.getDeprecatedKeys().iterator();

                while (var4.hasNext()) {
                    String deprecatedKey = (String) var4.next();
                    deprecatedNames.add("<" + deprecatedKey + ">");
                }

                messageBuilder.append(Joiner.on(", ").join(deprecatedNames));
                messageBuilder.append(")");
            }

            return messageBuilder.toString();
        }

        public UnsupportedValueException(EntryFromConfigurationBuilder.UsedProperties property, String value) {
            super(createMessage(property, value));
            this.property = property;
            this.value = value;
        }
    }
}
