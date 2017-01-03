package io.sugo.pio.spark.connections;

import io.sugo.pio.spark.HadoopTools;
import io.sugo.pio.spark.connections.KeyValueEnableElement.KVEEMap;
import org.apache.hadoop.conf.Configuration;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;


/**
 */
public class HadoopConnectionEntry implements Comparable<HadoopConnectionEntry> {
    private Configuration configuration = null;
    private String name = "";
    private String masterAddress = "";
    private int mapredPort = 0;
    private int hdfsPort = 0;
    private int jobHistoryServerPort = 0;
    private String jobtrackerAddress = "";
    private String namenodeAddress = "";
    private String jobHistoryServerAddress = "";
    private boolean singleMasterConfiguration = true;
    private String hadoopUsername;
    private KVEEMap advancedHadoopSettings;
    private String executionFramework;
    private String libDirectory;

    public HadoopConnectionEntry() {
        this.advancedHadoopSettings = new KVEEMap();
        this.executionFramework = "yarn";
        this.hadoopUsername = "";
        this.name = "";
        this.masterAddress = "";
        this.jobtrackerAddress = "";
        this.namenodeAddress = "";
        this.jobHistoryServerAddress = "";
        this.singleMasterConfiguration = true;
        this.libDirectory = "";
        this.hadoopUsername = HadoopTools.getNormalizedOSUsername();
    }

    public static HadoopConnectionEntry copyEntry(HadoopConnectionEntry radoopConnectionEntry) {
        HadoopConnectionEntry result = new HadoopConnectionEntry();
        result.name = radoopConnectionEntry.name;
        result.masterAddress = radoopConnectionEntry.masterAddress;
        result.mapredPort = radoopConnectionEntry.mapredPort;
        result.hdfsPort = radoopConnectionEntry.hdfsPort;
        result.jobHistoryServerPort = radoopConnectionEntry.jobHistoryServerPort;
        result.jobtrackerAddress = radoopConnectionEntry.jobtrackerAddress;
        result.namenodeAddress = radoopConnectionEntry.namenodeAddress;
        result.jobHistoryServerAddress = radoopConnectionEntry.jobHistoryServerAddress;
        result.singleMasterConfiguration = radoopConnectionEntry.singleMasterConfiguration;

        result.advancedHadoopSettings = new KVEEMap();
        Iterator<KeyValueEnableElement> iterator = radoopConnectionEntry.advancedHadoopSettings.values().iterator();

        KeyValueEnableElement kvee;
        while (iterator.hasNext()) {
            kvee = iterator.next();
            result.advancedHadoopSettings.add(new KeyValueEnableElement(kvee.key, kvee.value, kvee.enabled));
        }

        result.libDirectory = radoopConnectionEntry.libDirectory;
        result.hadoopUsername = radoopConnectionEntry.getHadoopUsername();
        result.executionFramework = radoopConnectionEntry.getExecutionFramework();
        return result;
    }

    @Override
    public int compareTo(HadoopConnectionEntry o) {
        return name.compareTo(o.name);
    }

    public String getJobTrackerAddress() {
        return !this.singleMasterConfiguration ? jobtrackerAddress : masterAddress;
    }

    public String getNameNodeAddress() {
        return !this.singleMasterConfiguration ? namenodeAddress : masterAddress;
    }

    public int getNameNodePort() {
        return hdfsPort;
    }

    public int getJobTrackerPort() {
        return mapredPort;
    }

    public String getJobHistoryServerAddress() {
        return !singleMasterConfiguration?jobHistoryServerAddress:masterAddress;
    }

    public int getJobHistoryServerPort() {
        return jobHistoryServerPort;
    }

    public String getJobHistoryPropertyValue() {
        String address = getJobHistoryServerAddress();
        int port = getJobHistoryServerPort();
        return address + ":" + port;
    }

    public String getNameNodeConnectionString() {
        return "hdfs://" + getNameNodeAddress() + ":" + getNameNodePort();
    }

    public String getJobTrackerConnectionString() {
        return getJobTrackerAddress() + ":" + getJobTrackerPort();
    }

    public String getExecutionFramework() {
        return executionFramework;
    }

    public boolean isSingleMasterConfiguration() {
        return singleMasterConfiguration;
    }

    public String getJobTrackerAddressMultipleMasters() {
        return jobtrackerAddress;
    }

    public String getNameNodeAddressMultipleMasters() {
        return namenodeAddress;
    }

    public String getJobHistoryServerAddressMultipleMasters() {
        return jobHistoryServerAddress;
    }

    public KVEEMap getAdvancedHadoopSettings() {
        return advancedHadoopSettings;
    }

    protected void setSingleMasterConfiguration(boolean singleMasterConfiguration) {
        this.singleMasterConfiguration = singleMasterConfiguration;
    }

    protected void addAdvancedHadoopSetting(KeyValueEnableElement setting) {
        advancedHadoopSettings.putIfAbsent(setting);
    }

    protected void setMasterAddress(String masterAddress) {
        this.masterAddress = masterAddress;
    }

    protected void setJobtrackerAddress(String jobtrackerAddress) {
        this.jobtrackerAddress = jobtrackerAddress;
    }

    protected void setNamenodeAddress(String namenodeAddress) {
        this.namenodeAddress = namenodeAddress;
    }

    protected void setJobHistoryServerAddress(String jobHistoryServerAddress) {
        this.jobHistoryServerAddress = jobHistoryServerAddress;
    }

    protected void setMapredPort(int mapredPort) {
        this.mapredPort = mapredPort;
    }

    protected void setHdfsPort(int hdfsPort) {
        this.hdfsPort = hdfsPort;
    }

    protected void setJobHistoryServerPort(int jobHistoryServerPort) {
        this.jobHistoryServerPort = jobHistoryServerPort;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String getHadoopUsername() {
        return hadoopUsername;
    }

    public void setHadoopUsername(String hadoopUsername) {
        this.hadoopUsername = hadoopUsername;
    }

    public Configuration getConfiguration() {
        if (configuration == null) {
            Configuration newConfiguration = new Configuration();
            newConfiguration.setClassLoader(this.getClass().getClassLoader());
            HashSet filteredPropertyKeys = new HashSet();
            if (!addAdvancedPropertyIfSet("fs.default.name", newConfiguration) && !addAdvancedPropertyIfSet("fs.defaultFS", newConfiguration)) {
                newConfiguration.set("fs.defaultFS", getNameNodeConnectionString());
            }

            filteredPropertyKeys.add("fs.defaultFS");
            filteredPropertyKeys.add("fs.default.name");
            if (!advancedHadoopSettings.hasValue("yarn.resourcemanager.address")) {
                newConfiguration.set("yarn.resourcemanager.address", getJobTrackerConnectionString());
            }

            if (!advancedHadoopSettings.hasValue("mapreduce.jobhistory.address")) {
                newConfiguration.set("mapreduce.jobhistory.address", getJobHistoryPropertyValue());
            }

            for(Map.Entry<String, KeyValueEnableElement> entry: advancedHadoopSettings.entrySet()) {
                if(!filteredPropertyKeys.contains(entry.getKey()) && entry.getValue().enabled) {
                    newConfiguration.set(entry.getValue().key, entry.getValue().value);
                }
            }
            configuration = newConfiguration;
        }
        return configuration;
    }

    private boolean addAdvancedPropertyIfSet(String key, Configuration configuration) {
        if(!advancedHadoopSettings.hasValue(key)) {
            return false;
        } else {
            configuration.set(key, advancedHadoopSettings.getValue(key));
            return true;
        }
    }

    public String getName() {
        return name;
    }

}
