package io.sugo.pio.operator.processing.filter;

public enum ScanResult {
    REMOVE, KEEP, UNCHECKED;

    public ScanResult invert(boolean invert) {
        switch (this) {
            case KEEP:
                return (invert) ? REMOVE : KEEP;
            case REMOVE:
                return (invert) ? KEEP : REMOVE;
            default:
                return UNCHECKED;
        }
    }
}