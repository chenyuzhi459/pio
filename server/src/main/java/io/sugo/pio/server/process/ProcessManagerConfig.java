package io.sugo.pio.server.process;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProcessManagerConfig {

    @JsonProperty
    private int executeMaxThread = 2;

    @JsonProperty
    private int processQueueSize = 100;

    @JsonProperty
    private int maxEntriesSize = 1000;

    public int getExecuteMaxThread() {
        return executeMaxThread;
    }

    public int getProcessQueueSize() {
        return processQueueSize;
    }

    public int getMaxEntriesSize() {
        return maxEntriesSize;
    }

    public void setExecuteMaxThread(int executeMaxThread) {
        this.executeMaxThread = executeMaxThread;
    }

    @Override
    public String toString() {
        return String.format("%s {executeMaxThread=%d, processQueueSize=%d}",
                ProcessManagerConfig.class.getSimpleName(), executeMaxThread, processQueueSize);
    }
}
