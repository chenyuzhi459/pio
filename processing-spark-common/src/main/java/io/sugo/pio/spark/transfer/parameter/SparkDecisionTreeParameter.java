package io.sugo.pio.spark.transfer.parameter;


/**
 */
public class SparkDecisionTreeParameter extends SparkParameter {
    private String impurity;
    private int maxDepth;
    private int minInstances;
    private double minGain;
    private int maxBins;
    private int maxMemoryInMB;
    private double subsamplingRate;
    private boolean useNodeIdCache;
    private boolean useBinominalMappings;

    public SparkDecisionTreeParameter() {
    }

    public String getImpurity() {
        return impurity;
    }

    public void setImpurity(String impurity) {
        this.impurity = impurity;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getMinInstances() {
        return minInstances;
    }

    public void setMinInstances(int minInstances) {
        this.minInstances = minInstances;
    }

    public double getMinGain() {
        return minGain;
    }

    public void setMinGain(double minGain) {
        this.minGain = minGain;
    }

    public int getMaxBins() {
        return maxBins;
    }

    public void setMaxBins(int maxBins) {
        this.maxBins = maxBins;
    }

    public int getMaxMemoryInMB() {
        return maxMemoryInMB;
    }

    public void setMaxMemoryInMB(int maxMemoryInMB) {
        this.maxMemoryInMB = maxMemoryInMB;
    }

    public double getSubsamplingRate() {
        return subsamplingRate;
    }

    public void setSubsamplingRate(double subsamplingRate) {
        this.subsamplingRate = subsamplingRate;
    }

    public boolean isUseNodeIdCache() {
        return useNodeIdCache;
    }

    public void setUseNodeIdCache(boolean useNodeIdCache) {
        this.useNodeIdCache = useNodeIdCache;
    }

    public boolean isUseBinominalMappings() {
        return useBinominalMappings;
    }

    public void setUseBinominalMappings(boolean useBinominalMappings) {
        this.useBinominalMappings = useBinominalMappings;
    }
}
